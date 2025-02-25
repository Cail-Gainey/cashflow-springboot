package cashflow.common.Jwt;


import cashflow.model.entity.User;
import cashflow.service.UserService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Optional;

/**
 * @author Cail Gainey
 * @since 2025/1/25 14:57
 **/
@Slf4j
@Component
public class JwtTokenUtils {

    private static final int TOKEN_EXPIRY_HOURS = 3;
    private static UserService staticUsersService;
    @Resource
    private UserService userService;

    /**
     * 生成token
     */
    public static String genToken(String userId, String password) {
        return JWT.create()
                .withAudience(userId) // 将 userId 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), TOKEN_EXPIRY_HOURS))
                .sign(Algorithm.HMAC256(password)); // 以 password 作为 token 的密钥
    }

    /**
     * 获取当前登录的用户信息
     */
    public static Optional<User> getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = Optional.ofNullable(request.getHeader("token"))
                .filter(StrUtil::isNotBlank)
                .orElse(request.getParameter("token"));

        if (StrUtil.isBlank(token)) {
            log.error("获取当前登录的token失败， token: {}", token);
            return Optional.empty();
        }

        try {
            String adminId = JWT.decode(token).getAudience().get(0);
            if (adminId != null) {
                return Optional.ofNullable(staticUsersService.getById(Integer.valueOf(adminId)));
            }
        } catch (Exception e) {
            log.error("获取当前用户信息失败, token={}", token, e);
        }
        return Optional.empty();
    }

    @PostConstruct
    public void setUserService() {
        staticUsersService = userService;
    }
}