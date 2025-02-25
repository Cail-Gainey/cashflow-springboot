package cashflow.service.impl;

import cashflow.dao.FundDao;
import cashflow.model.dto.FundDto;
import cashflow.model.entity.Fund;
import cashflow.model.vo.FundVo;
import cashflow.service.FundService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 资金账户表(Fund)表服务实现类
 *
 * @author Cail Gainey
 * @since 2025-01-21 19:17:56
 */
@Transactional(rollbackFor = Exception.class)
@Service("fundService")
public class FundServiceImpl extends ServiceImpl<FundDao, Fund> implements FundService {

    @Override
    public List<FundVo> listAllById(@NonNull Integer userId) {
        LambdaQueryWrapper<Fund> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Fund::getUserId, userId);
        List<Fund> list = this.list(lqw);

        return list.stream()
                .map(fund -> FundVo.builder()
                        .id(fund.getId())
                        .name(fund.getName())
                        .balance(fund.getBalance())
                        .remark(fund.getRemark())
                        .isCount(fund.getIsCount())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void addDefaultFund(@NonNull Integer userId) {
        Fund defaultFund = new Fund();
        defaultFund.setUserId(userId);
        save(defaultFund);
    }

    @Override
    public void updateByDto(@NonNull FundDto dto, Integer id) {
        LambdaUpdateWrapper<Fund> luw = new LambdaUpdateWrapper<>();
        luw.eq(Fund::getId, id);

        Optional.ofNullable(dto.getName()).ifPresent(name -> luw.set(Fund::getName, name));
        Optional.ofNullable(dto.getBalance()).ifPresent(balance -> luw.set(Fund::getBalance, balance));
        Optional.ofNullable(dto.getRemark()).ifPresent(remark -> luw.set(Fund::getRemark, remark));
        Optional.ofNullable(dto.getIsCount()).ifPresent(isCount -> luw.set(Fund::getIsCount, isCount));

        update(luw);
    }

    @Override
    public Integer getDefaultId(@NonNull Integer userId) {
        LambdaQueryWrapper<Fund> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Fund::getName, "默认资产").eq(Fund::getUserId, userId);
        Fund fund = this.getOne(lqw);
        if (fund == null) {
            throw new RuntimeException("未找到该用户：" + userId + "的默认资产");
        }
        return fund.getId();
    }
}