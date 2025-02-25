package cashflow.service;

import cashflow.model.entity.User;
import cashflow.model.vo.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户表(User)表服务接口
 *
 * @author Cail Gainey
 * @since 2025-01-19 21:48:25
 */
public interface UserService extends IService<User> {
    UserVo login(String email, String password);

    UserVo getByUserId(Integer id);

    Integer countUsers(String email);

}

