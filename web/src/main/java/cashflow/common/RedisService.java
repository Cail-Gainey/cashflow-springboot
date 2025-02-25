package cashflow.common;

import cashflow.exception.RedisUpdateException;
import cashflow.model.vo.UserVo;
import cashflow.service.FileService;
import cashflow.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 服务类，提供与 Redis 交互的方法
 *
 * @author Cail Gainey
 * @since 2025/1/25 14:58
 **/
@Slf4j
@Service
public class RedisService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int NO_REMEMBER_ME_TIME = 3;
    private static final int IS_REMEMBER_ME_TIME = 72;
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 保存用户 Token 到 Redis，并设置过期时间
     *
     * @param token      用户的 Token
     * @param userInfo   用户信息
     * @param rememberMe 是否记住我
     */
    public void saveUserToken(@NonNull String token, @NonNull String userInfo, boolean rememberMe) {
        redisTemplate.opsForValue().set(token, userInfo, rememberMe ? IS_REMEMBER_ME_TIME : NO_REMEMBER_ME_TIME, TimeUnit.HOURS);
    }

    /**
     * 根据 Token 获取用户信息
     *
     * @param token 用户的 Token
     * @return 用户信息，如果 Token 不存在则返回 null
     */
    public String getUserInfoByToken(@NonNull String token) {
        return redisTemplate.opsForValue().get(token);
    }

    /**
     * 删除指定的用户 Token
     *
     * @param token 用户的 Token
     */
    public void removeUser(@NonNull String token) {
        redisTemplate.delete(token);
    }

    /**
     * 根据 token 和新的用户信息更新 Redis 中的值，同时保持过期时间不变
     *
     * @param token  用户的 Token
     * @param userId 用户的 ID
     */
    public void updateUserInfo(@NonNull String token, Integer userId) {
        try {
            // 获取新的用户信息
            UserVo user = userService.getByUserId(userId);
            user.setAvatar(fileService.getFullFileUrl(user.getAvatar()));
            String jsonUser = MAPPER.writeValueAsString(user);

            // 获取当前 Redis 中存储的过期时间
            Long expirationTime = redisTemplate.getExpire(token, TimeUnit.SECONDS);

            if (expirationTime != null && expirationTime > 0) {
                // 更新用户信息并保留原有的过期时间
                redisTemplate.opsForValue().set(token, jsonUser, expirationTime, TimeUnit.SECONDS);
            } else {
                log.warn("Token {} 已经过期或不存在，无法更新", token);
                throw new RedisUpdateException("Token 已过期或不存在，无法更新");
            }
        } catch (JsonProcessingException e) {
            log.error("Json 转换失败，更新用户信息失败", e);
            throw new RedisUpdateException("Json 转换失败，更新用户信息失败", e);
        }
    }
}