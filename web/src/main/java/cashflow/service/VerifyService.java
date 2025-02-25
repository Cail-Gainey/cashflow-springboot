package cashflow.service;

import cashflow.model.dto.VerifyDto;

/**
 * 验证码服务接口
 *
 * @author Cail Gainey
 * @since 2025/1/25 15:01
 **/
public interface VerifyService {
    VerifyDto send();

    boolean verify(VerifyDto dto);
}
