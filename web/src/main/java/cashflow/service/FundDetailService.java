package cashflow.service;

import cashflow.model.dto.TransactionDto;
import cashflow.model.entity.FundDetail;
import cashflow.model.vo.FundDetailVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 资金明细表(FundDetail)表服务接口
 *
 * @author Cail Gainey
 * @since 2025-01-21 19:18:17
 */
public interface FundDetailService extends IService<FundDetail> {
    void saveByTransactionDto(TransactionDto dto, Integer transactionId);

    void updateByTransactionDto(TransactionDto dto, Integer transactionId);

    void cleanByLedgerId(Integer ledgerId);

    void cleanByFundId(Integer fundId);

    void shiftByLedgerId(Integer oldId, Integer newId);

    void transferCategory(Integer oldId, Integer newId);

    List<FundDetailVo> listByFundId(Integer userId, Integer fundId);
}

