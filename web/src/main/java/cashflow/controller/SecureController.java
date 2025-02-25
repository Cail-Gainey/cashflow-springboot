package cashflow.controller;

import cashflow.common.R;
import cashflow.common.RedisService;
import cashflow.enums.MessageEnums;
import cashflow.model.vo.UserVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 需要登录后携带JWT才能访问
 *
 * @author Cail Gainey
 * @since 2025/1/25 15:00
 **/
@Slf4j
@RestController
@RequestMapping("/api/secure")
public class SecureController {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TOKEN_HEADER = "token";
    @Resource
    private RedisService redisService;

    @GetMapping("/info")
    public R<?> getInfo(@RequestHeader(TOKEN_HEADER) String token) {
        if (token == null) {
            return R.error(MessageEnums.TOKEN_VERITY_ERROR);
        }
        // 根据 token 获取用户信息
        String userInfo = redisService.getUserInfoByToken(token);

        try {
            UserVo users = MAPPER.readValue(userInfo, UserVo.class);
            return R.success(users);
        } catch (Exception e) {
            log.error("解析异常", e);
            return R.error();
        }
    }
}