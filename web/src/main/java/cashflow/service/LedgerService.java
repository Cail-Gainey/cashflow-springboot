package cashflow.service;

import cashflow.model.entity.Ledger;
import cashflow.model.vo.LedgerVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 账本表(Ledger)表服务接口
 *
 * @author Cail Gainey
 * @since 2025-01-21 20:08:11
 */
public interface LedgerService extends IService<Ledger> {
    List<LedgerVo> listAllById(Integer userId);

    void addDefaultLedger(Integer userId);

    void removeLedger(Integer id);
}

