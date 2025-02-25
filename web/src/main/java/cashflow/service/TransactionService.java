package cashflow.service;

import cashflow.model.entity.Transaction;
import cashflow.model.vo.TransactionVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 账目表(Transaction)表服务接口
 *
 * @author Cail Gainey
 * @since 2025-01-21 15:43:56
 */
public interface TransactionService extends IService<Transaction> {
    List<TransactionVo> listAllById(Integer userId, Integer ledgerId);

    void cleanByLedgerId(Integer userId, Integer ledgerId);

    void cleanByFundId(Integer userId, Integer fundId);

    void shiftByLedgerId(Integer userId, Integer oldId, Integer newId);

    Integer countCategory(Integer categoryId);

    void transferCategory(Integer oldId, Integer newId);
}

