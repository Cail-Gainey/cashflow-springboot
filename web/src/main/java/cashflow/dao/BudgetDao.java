package cashflow.dao;

import cashflow.model.entity.Budget;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预算表(Budget)表数据库访问层
 *
 * @author Cail Gainey
 * @since 2025-01-21 15:44:08
 */
@Mapper
public interface BudgetDao extends BaseMapper<Budget> {

}

