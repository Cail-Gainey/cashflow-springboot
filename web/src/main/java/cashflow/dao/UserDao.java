package cashflow.dao;

import cashflow.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表(User)表数据库访问层
 *
 * @author Cail Gainey
 * @since 2025-01-19 21:48:25
 */
@Mapper
public interface UserDao extends BaseMapper<User> {

}

