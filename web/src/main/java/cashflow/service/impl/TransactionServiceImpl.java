package cashflow.service.impl;

import cashflow.dao.CategoryDao;
import cashflow.dao.FundDao;
import cashflow.dao.LedgerDao;
import cashflow.dao.TransactionDao;
import cashflow.exception.LedgerNotFoundException;
import cashflow.model.entity.Ledger;
import cashflow.model.entity.Transaction;
import cashflow.model.vo.TransactionVo;
import cashflow.service.TransactionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 账目表(Transaction)表服务实现类
 *
 * @author Cail Gainey
 * @since 2025-01-21 15:43:56
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service("transactionService")
public class TransactionServiceImpl extends ServiceImpl<TransactionDao, Transaction> implements TransactionService {
    @Resource
    private TransactionDao transactionDao;
    @Resource
    private CategoryDao categoryDao;
    @Resource
    private FundDao fundDao;
    @Resource
    private LedgerDao ledgerDao;

    @Override
    public List<TransactionVo> listAllById(@NonNull Integer userId, @NonNull Integer ledgerId) {
        LambdaQueryWrapper<Transaction> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Transaction::getUserId, userId).eq(Transaction::getLedgerId, ledgerId);
        List<Transaction> list = transactionDao.selectList(lqw);

        return list.stream().map(transaction -> TransactionVo.builder()
                .id(transaction.getId())
                .ledger(ledgerDao.getName(transaction.getLedgerId()))
                .fundId(transaction.getFundId())
                .fundName(fundDao.getName(transaction.getFundId()))
                .type(transaction.getType())
                .categoryName(categoryDao.getName(transaction.getCategoryId()))
                .categoryId(transaction.getCategoryId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .time(transaction.getTime())
                .build()
        ).toList();
    }


    @Override
    public void cleanByLedgerId(@NonNull Integer userId, @NonNull Integer ledgerId) {
        LambdaQueryWrapper<Transaction> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Transaction::getUserId, userId).eq(Transaction::getLedgerId, ledgerId);
        transactionDao.delete(lqw);
    }

    @Override
    public void cleanByFundId(Integer userId, Integer fundId) {
        LambdaQueryWrapper<Transaction> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Transaction::getUserId, userId).eq(Transaction::getFundId, fundId);
        transactionDao.delete(lqw);
    }

    @Override
    public void shiftByLedgerId(@NonNull Integer userId, @NonNull Integer oldId, @NonNull Integer newId) {
        LambdaQueryWrapper<Ledger> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Ledger::getId, newId);
        // 检查 newId 是否已存在
        if (!ledgerDao.exists(lqw)) {
            throw new LedgerNotFoundException("账本不存在");
        }

        LambdaUpdateWrapper<Transaction> luw = new LambdaUpdateWrapper<>();
        luw.eq(Transaction::getUserId, userId).eq(Transaction::getLedgerId, oldId)
                .set(Transaction::getLedgerId, newId);

        int affectedRows = transactionDao.update(luw);
        if (affectedRows == 0) {
            log.error("记账详情迁移失败");
            throw new LedgerNotFoundException("更新失败，可能是因为旧账本不存在");
        }
    }

    @Override
    public Integer countCategory(Integer categoryId) {
        LambdaQueryWrapper<Transaction> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Transaction::getCategoryId, categoryId);
        return Math.toIntExact(this.count(lqw));
    }

    @Override
    public void transferCategory(Integer oldId, Integer newId) {
        LambdaUpdateWrapper<Transaction> luw = new LambdaUpdateWrapper<>();
        luw.eq(Transaction::getCategoryId, oldId).set(Transaction::getCategoryId, newId);
        transactionDao.update(luw);
    }
}

