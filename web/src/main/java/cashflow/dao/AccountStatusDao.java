package cashflow.dao;

import cashflow.model.entity.AccountStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * (AccountStatus)表数据库访问层
 *
 * @author Cail Gainey
 * @since 2025-02-18 16:16:10
 */
@Mapper
public interface AccountStatusDao extends BaseMapper<AccountStatus> {
    @Select("select id from account_status where user_id = #{userId} and status = -1")
    Integer isBlackAccount(Integer userId);
}

