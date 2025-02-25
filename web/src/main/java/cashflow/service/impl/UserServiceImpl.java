package cashflow.service.impl;

import cashflow.dao.AccountStatusDao;
import cashflow.dao.UserDao;
import cashflow.enums.MessageEnums;
import cashflow.exception.AccountStatusException;
import cashflow.model.entity.User;
import cashflow.model.vo.UserVo;
import cashflow.service.UserService;
import cashflow.utils.AESUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户表(User)表服务实现类
 *
 * @author Cail Gainey
 * @since 2025-01-19 21:48:25
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private AccountStatusDao statusDao;

    @Override
    public UserVo login(String email, String password) {
        User user = findUserByEmail(email);
        if (user == null) {
            return null;
        }

        Integer isBlackAccount = statusDao.isBlackAccount(user.getId());

        if (isBlackAccount != null) {
            throw new AccountStatusException(MessageEnums.USER_WAS_BAN.getMessage());
        }

        try {
            String decryptPassword = AESUtil.decrypt(user.getPassword());
            if (decryptPassword.equals(password)) {
                return buildUserVo(user);
            }
        } catch (Exception e) {
            log.error("Password decryption error for email: {}", email, e);
        }
        return null;
    }

    @Override
    public UserVo getByUserId(Integer id) {
        User user = getById(id);
        return user == null ? null : buildUserVo(user);
    }

    @Override
    public Integer countUsers(String email) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getEmail, email).select(User::getId);
        long count = this.count(lqw);
        return (count > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) count;
    }

    private User findUserByEmail(String email) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getEmail, email);
        return userDao.selectOne(lqw);
    }

    private UserVo buildUserVo(@NotNull User user) {
        return UserVo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .sex(user.getSex())
                .avatar(user.getAvatar())
                .createdTime(user.getCreatedAt())
                .build();
    }
}