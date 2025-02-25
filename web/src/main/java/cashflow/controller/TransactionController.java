package cashflow.controller;

import cashflow.common.R;
import cashflow.exception.CategoryException;
import cashflow.model.dto.TransactionDto;
import cashflow.model.entity.Transaction;
import cashflow.model.vo.TransactionVo;
import cashflow.service.CategoryService;
import cashflow.service.FundDetailService;
import cashflow.service.TransactionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:01
 **/
@Slf4j
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    @Resource
    private TransactionService transactionService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private FundDetailService detailService;

    @GetMapping("/{userId}/{ledgerId}")
    public R<List<TransactionVo>> listAllById(@PathVariable @NonNull Integer userId, @PathVariable @NonNull Integer ledgerId) {
        List<TransactionVo> list = transactionService.listAllById(userId, ledgerId);
        return R.success(list);
    }

    @GetMapping("/{userId}/{categoryId}/count")
    public R<Integer> countCategory(@NonNull @PathVariable Integer userId, @NonNull @PathVariable Integer categoryId) {
        LambdaQueryWrapper<Transaction> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Transaction::getUserId, userId).eq(Transaction::getCategoryId, categoryId);
        Integer count = Math.toIntExact(transactionService.count(lqw));
        return R.success(count);
    }

    @Transactional
    @PostMapping("/{userId}")
    public R<String> save(@RequestBody @NonNull TransactionDto dto, @PathVariable @NonNull Integer userId) {
        return handleTransaction(dto, userId, null);
    }

    @Transactional
    @PutMapping("/{id}")
    public R<String> update(@RequestBody @NonNull TransactionDto dto, @PathVariable @NonNull Integer id) {
        return handleTransaction(dto, null, id);
    }


    private R<String> handleTransaction(@NotNull TransactionDto dto, Integer userId, Integer id) {
        try {
            categoryService.checkCategoryType(dto.getType(), dto.getCategoryId());
            Transaction transaction = buildTransaction(dto, userId, id);
            boolean success = (id == null) ? transactionService.save(transaction) : transactionService.update(transaction, new LambdaQueryWrapper<Transaction>().eq(Transaction::getId, id));
            if (success) {
                if (id == null) {
                    detailService.saveByTransactionDto(dto, transaction.getId());
                } else {
                    detailService.updateByTransactionDto(dto, id);
                }
                return R.success();
            } else {
                return R.error();
            }
        } catch (CategoryException e) {
            log.error("修改账单失败", e);
            return R.error(e.getMessage());
        }
    }

    private Transaction buildTransaction(@NotNull TransactionDto dto, Integer userId, Integer id) {
        return Transaction.builder()
                .userId(userId)
                .ledgerId(dto.getLedgerId())
                .fundId(dto.getFundId())
                .type(dto.getType())
                .categoryId(dto.getCategoryId())
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .time(dto.getTime())
                .build();
    }

    @DeleteMapping("/{id}")
    public R<String> remove(@PathVariable @NonNull Integer id) {
        try {
            boolean removed = transactionService.removeById(id);
            return removed ? R.success() : R.error();
        } catch (CategoryException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }
}