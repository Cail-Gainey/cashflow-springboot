package cashflow.dao;

import cashflow.model.entity.FundDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.NonNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 资金明细表(FundDetail)表数据库访问层
 *
 * @author Cail Gainey
 * @since 2025-01-21 19:18:17
 */
@Mapper
public interface FundDetailDao extends BaseMapper<FundDetail> {

    @Select("select name from fund where id = #{id}")
    String getName(@NonNull Integer id);
}

