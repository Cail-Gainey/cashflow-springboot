package cashflow.service;

import cashflow.model.entity.AccountStatus;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * (AccountStatus)表服务接口
 *
 * @author Cail Gainey
 * @since 2025-02-18 16:16:11
 */
public interface AccountStatusService extends IService<AccountStatus> {
    void saveStatus(String token, Integer status);
}

