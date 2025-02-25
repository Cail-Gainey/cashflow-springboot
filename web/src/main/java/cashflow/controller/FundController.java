package cashflow.controller;

import cashflow.common.R;
import cashflow.enums.MessageEnums;
import cashflow.model.dto.FundDto;
import cashflow.model.entity.Fund;
import cashflow.model.vo.FundVo;
import cashflow.service.FundDetailService;
import cashflow.service.FundService;
import cashflow.service.TransactionService;
import cashflow.tools.ValidateTools;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 资金账户表(Fund)表控制层
 *
 * @author Cail Gainey
 * @since 2025-01-21 19:17:56
 */
@Slf4j
@RestController
@RequestMapping("/api/fund")
public class FundController {
    @Resource
    private FundService fundService;
    @Resource
    private FundDetailService detailService;
    @Resource
    private TransactionService transactionService;

    private @NotNull LambdaQueryWrapper<Fund> buildFundQueryWrapper(Integer userId, Integer id) {
        LambdaQueryWrapper<Fund> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Fund::getUserId, userId);
        if (id != null) {
            lqw.eq(Fund::getId, id);
        }
        return lqw;
    }

    @GetMapping("/{userId}")
    public R<List<FundVo>> listAllById(@PathVariable @NonNull Integer userId) {
        List<FundVo> list = fundService.listAllById(userId);
        return R.success(list);
    }

    @PostMapping("/{userId}")
    public R<String> save(@NonNull @RequestBody FundDto dto, @NonNull @PathVariable Integer userId) {
        return Optional.ofNullable(ValidateTools.validateFundDto(dto))
                .orElseGet(() -> {
                    LambdaQueryWrapper<Fund> lqw = new LambdaQueryWrapper<>();
                    lqw.eq(Fund::getName, dto.getName());

                    if (fundService.exists(lqw)) {
                        return R.error(MessageEnums.FUND_EXIST);
                    }

                    Fund fund = Fund.builder()
                            .userId(userId)
                            .name(dto.getName())
                            .remark(dto.getRemark())
                            .balance(dto.getBalance())
                            .isCount(dto.getIsCount())
                            .build();

                    boolean saved = fundService.save(fund);
                    return saved ? R.success() : R.error();
                });
    }

    @PutMapping("/{userId}/{id}")
    public R<String> update(@NonNull @RequestBody FundDto dto, @NonNull @PathVariable Integer userId, @NonNull @PathVariable Integer id) {
        return Optional.ofNullable(ValidateTools.validateFundDto(dto))
                .orElseGet(() -> {
                    try {
                        Fund dbFund = fundService.getById(id);
                        if (Objects.equals(dbFund.getName(), dto.getName())) {
                            fundService.updateByDto(dto, id);
                        } else {
                            LambdaQueryWrapper<Fund> lqw = new LambdaQueryWrapper<>();
                            lqw.eq(Fund::getName, dto.getName()).last("LIMIT 1");

                            if (fundService.exists(lqw)) {
                                return R.error(MessageEnums.FUND_EXIST);
                            }
                        }
                        return R.success();
                    } catch (Exception e) {
                        log.error("修改资金失败", e);
                        return R.error(MessageEnums.SERVER_FAILURE);
                    }
                });
    }

    @Transactional
    @DeleteMapping("/{userId}/{id}")
    public R<String> remove(@NonNull @PathVariable Integer userId, @NonNull @PathVariable Integer id) {
        try {
            // 判断资金是否存在
            LambdaQueryWrapper<Fund> lqw = buildFundQueryWrapper(userId, id);
            if (!fundService.exists(lqw)) {
                return R.error(MessageEnums.FUND_NO_FUND);
            }

            // 默认不允许删除
            Integer defaultId = fundService.getDefaultId(userId);
            if (Objects.equals(defaultId, id)) {
                return R.error(MessageEnums.DEFAULT_FUND_CANT_DELETE);
            }

            // 清理关联数据
            transactionService.cleanByFundId(userId, id);
            detailService.cleanByFundId(id);

            // 删除资金记录
            boolean fundRemoved = fundService.remove(lqw);
            if (!fundRemoved) {
                log.error("删除资金失败");
                return R.error(MessageEnums.SERVER_FAILURE);
            }

            return R.success();
        } catch (Exception e) {
            log.error("删除资金操作失败，已回滚", e);
            return R.error(MessageEnums.SERVER_FAILURE);
        }
    }
}