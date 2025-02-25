package cashflow.dao;

import cashflow.model.entity.Ledger;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 账本表(Ledger)表数据库访问层
 *
 * @author Cail Gainey
 * @since 2025-01-21 20:08:11
 */
@Mapper
public interface LedgerDao extends BaseMapper<Ledger> {
    @Select("select name from ledger where id = #{ledgerId} limit 1")
    String getName(Integer ledgerId);
}

