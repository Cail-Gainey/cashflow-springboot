package cashflow.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务类，提供用户 Token 的存储、获取和删除操作。
 *
 * @author Cail Gainey
 * @since 2025/1/25 15:04
 **/
@Slf4j
@Service
public class RedisService {

    @Resource
    private StringRedisTemplate redisTemplate;

    /**
     * 将用户的 Token 和信息保存到 Redis 中，并设置过期时间为 3 小时。
     *
     * @param token    用户 Token
     * @param userInfo 用户信息
     * @throws IllegalArgumentException 如果 Token 或用户信息为空
     */
    public void saveUserToken(String token, String userInfo) {
        Optional.ofNullable(token).orElseThrow(() -> new IllegalArgumentException("Token 不能为空"));
        Optional.ofNullable(userInfo).orElseThrow(() -> new IllegalArgumentException("用户信息不能为空"));
        redisTemplate.opsForValue().set(token, userInfo, 3, TimeUnit.HOURS);
    }

    /**
     * 根据 Token 获取存储在 Redis 中的用户信息。
     *
     * @param token 用户 Token
     * @return 用户信息，如果 Token 不存在则返回 null
     */
    public String getUserInfoByToken(String token) {
        return redisTemplate.opsForValue().get(token);
    }

    /**
     * 删除指定的用户 Token 信息。
     *
     * @param token 用户 Token
     * @throws IllegalArgumentException 如果 Token 为空
     */
    public void removeUserToken(String token) {
        Optional.ofNullable(token).orElseThrow(() -> new IllegalArgumentException("Token 不能为空"));
        redisTemplate.delete(token);
    }
}