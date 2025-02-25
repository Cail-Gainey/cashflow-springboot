package cashflow.dao;

import cashflow.model.entity.Fund;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.NonNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 资金账户表(Fund)表数据库访问层
 *
 * @author Cail Gainey
 * @since 2025-01-21 19:17:56
 */
@Mapper
public interface FundDao extends BaseMapper<Fund> {
    @Select("select name from fund where id = #{fundId} limit 1")
    String getName(@NonNull Integer fundId);

    @Select("select id from fund where user_id = #{userId}")
    List<Integer> selectIds(@NonNull Integer userId);

    @Update("update fund set balance = #{balance} where id = #{id}")
    void setBalance(@NonNull BigDecimal balance, @NonNull Integer id);

    @Select("select balance  from fund where id = #{id}")
    BigDecimal getBalance(@NonNull Integer id);
}

