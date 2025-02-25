package cashflow.dao;

import cashflow.model.entity.Transaction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账目表(Transaction)表数据库访问层
 *
 * @author Cail Gainey
 * @since 2025-01-21 15:43:56
 */
@Mapper
public interface TransactionDao extends BaseMapper<Transaction> {

}

