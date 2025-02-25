package cashflow.service.impl;

import cashflow.dao.CategoryDao;
import cashflow.dao.FundDao;
import cashflow.dao.FundDetailDao;
import cashflow.dao.LedgerDao;
import cashflow.enums.MessageEnums;
import cashflow.exception.LedgerNotFoundException;
import cashflow.model.dto.TransactionDto;
import cashflow.model.entity.FundDetail;
import cashflow.model.entity.Ledger;
import cashflow.model.vo.FundDetailVo;
import cashflow.service.FundDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Cail Gainey
 * @since 2025-01-21 19:18:17
 **/
@Slf4j
@Service("fundDetailService")
@Transactional(rollbackFor = Exception.class)
public class FundDetailServiceImpl extends ServiceImpl<FundDetailDao, FundDetail> implements FundDetailService {
    @Resource
    private FundDao fundDao;
    @Resource
    private FundDetailDao detailDao;
    @Resource
    private LedgerDao ledgerDao;
    @Resource
    private CategoryDao categoryDao;

    @Override
    public void saveByTransactionDto(@NonNull TransactionDto dto, @NonNull Integer transactionId) {
        BigDecimal oldBalance = fundDao.getBalance(dto.getFundId());
        BigDecimal newBalance = calculateNewBalance(oldBalance, dto.getAmount(), dto.getType());
        fundDao.setBalance(newBalance, dto.getFundId());

        FundDetail detail = FundDetail.builder()
                .fundId(dto.getFundId())
                .transactionId(transactionId)
                .ledgerId(dto.getLedgerId())
                .categoryId(dto.getCategoryId())
                .type(dto.getType())
                .amount(dto.getAmount())
                .oldBalance(oldBalance)
                .newBalance(newBalance)
                .description(dto.getDescription())
                .time(dto.getTime())
                .build();
        this.save(detail);
    }

    @Override
    public void updateByTransactionDto(@NonNull TransactionDto dto, @NonNull Integer transactionId) {
        FundDetail oldDetail = Optional.ofNullable(getFundDetailByTransactionId(transactionId))
                .orElseThrow(() -> new RuntimeException("未找到交易记录"));

        BigDecimal oldBalance = calculateOldBalance(oldDetail.getNewBalance(), oldDetail.getAmount(), oldDetail.getType());

        LambdaUpdateWrapper<FundDetail> luw = new LambdaUpdateWrapper<>();
        luw.eq(FundDetail::getId, oldDetail.getId())
                .set(FundDetail::getLedgerId, dto.getLedgerId())
                .set(FundDetail::getFundId, dto.getFundId())
                .set(FundDetail::getCategoryId, dto.getCategoryId())
                .set(FundDetail::getType, dto.getType())
                .set(FundDetail::getDescription, dto.getDescription())
                .set(FundDetail::getAmount, dto.getAmount())
                .set(FundDetail::getOldBalance, oldBalance)
                .set(FundDetail::getNewBalance, calculateNewBalance(oldBalance, dto.getAmount(), dto.getType()))
                .set(FundDetail::getTime, dto.getTime())
                .set(FundDetail::getUpdatedAt, LocalDateTime.now());
        this.update(luw);
    }

    @Override
    public void cleanByLedgerId(Integer ledgerId) {
        LambdaQueryWrapper<FundDetail> lqw = new LambdaQueryWrapper<>();
        lqw.eq(FundDetail::getLedgerId, ledgerId);
        detailDao.delete(lqw);
    }

    @Override
    public void cleanByFundId(Integer fundId) {
        LambdaQueryWrapper<FundDetail> lqw = new LambdaQueryWrapper<>();
        lqw.eq(FundDetail::getFundId, fundId);
        detailDao.delete(lqw);
    }

    @Override
    public void shiftByLedgerId(@NonNull Integer oldId, @NonNull Integer newId) {
        LambdaQueryWrapper<Ledger> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Ledger::getId, newId);
        // 检查 newId 是否已存在
        if (!ledgerDao.exists(lqw)) {
            throw new LedgerNotFoundException("账本不存在");
        }

        LambdaUpdateWrapper<FundDetail> luw = new LambdaUpdateWrapper<>();
        luw.eq(FundDetail::getLedgerId, oldId).set(FundDetail::getLedgerId, newId);
        int affectedRows = detailDao.update(luw);
        if (affectedRows == 0) {
            log.error("资金详情迁移失败");
            throw new LedgerNotFoundException("更新失败，可能是因为旧账本 ID 不存在");
        }
    }

    @Override
    public void transferCategory(Integer oldId, Integer newId) {
        LambdaUpdateWrapper<FundDetail> luw = new LambdaUpdateWrapper<>();
        luw.eq(FundDetail::getCategoryId, oldId).set(FundDetail::getCategoryId, newId);
        detailDao.update(luw);
    }


    @Override
    public List<FundDetailVo> listByFundId(Integer userId, Integer fundId) {
        if (!isMyFund(userId, fundId)) {
            throw new RuntimeException(MessageEnums.FUND_NO_FUND.getMessage());
        }

        LambdaQueryWrapper<FundDetail> lqw = new LambdaQueryWrapper<>();
        lqw.eq(FundDetail::getFundId, fundId)
                .select(FundDetail::getTransactionId, FundDetail::getType, FundDetail::getAmount,
                        FundDetail::getNewBalance, FundDetail::getDescription, FundDetail::getTime,
                        FundDetail::getCategoryId, FundDetail::getLedgerId, FundDetail::getFundId);

        List<FundDetail> list = this.list(lqw);
        return list.stream().map(fundDetail -> FundDetailVo.builder()
                        .id(fundDetail.getTransactionId())
                        .categoryId(fundDetail.getCategoryId())
                        .ledgerId(fundDetail.getLedgerId())
                        .fundId(fundDetail.getFundId())
                        .fundName(detailDao.getName(fundDetail.getFundId()))
                        .categoryName(categoryDao.getName(fundDetail.getCategoryId()))
                        .type(fundDetail.getType())
                        .amount(fundDetail.getAmount())
                        .description(fundDetail.getDescription())
                        .time(fundDetail.getTime())
                        .build())
                .collect(Collectors.toList());
    }

    private boolean isMyFund(Integer userId, Integer fundId) {
        Set<Integer> ids = new HashSet<>(fundDao.selectIds(userId));
        return ids.contains(fundId);
    }

    private FundDetail getPreviousBalance(Integer fundId) {
        LambdaQueryWrapper<FundDetail> lqw = new LambdaQueryWrapper<>();
        lqw.eq(FundDetail::getFundId, fundId)
                .orderByDesc(FundDetail::getUpdatedAt)
                .last("LIMIT 1");
        return this.getOne(lqw);
    }

    private FundDetail getFundDetailByTransactionId(Integer transactionId) {
        LambdaQueryWrapper<FundDetail> lqw = new LambdaQueryWrapper<>();
        lqw.eq(FundDetail::getTransactionId, transactionId);
        return this.getOne(lqw);
    }

    private BigDecimal calculateNewBalance(BigDecimal oldBalance, BigDecimal amount, Integer type) {
        return switch (type) {
            case 1 -> oldBalance.subtract(amount);
            case 2 -> oldBalance.add(amount);
            default -> throw new IllegalArgumentException("无效的交易类型");
        };
    }

    private BigDecimal calculateOldBalance(BigDecimal oldBalance, BigDecimal amount, Integer type) {
        return switch (type) {
            case 1 -> oldBalance.add(amount);
            case 2 -> oldBalance.subtract(amount);
            default -> throw new IllegalArgumentException("无效的交易类型");
        };
    }
}