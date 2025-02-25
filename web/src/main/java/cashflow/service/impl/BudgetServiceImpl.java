package cashflow.service.impl;

import cashflow.dao.BudgetDao;
import cashflow.model.entity.Budget;
import cashflow.service.BudgetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 预算表(Budget)表服务实现类
 *
 * @author Cail Gainey
 * @since 2025-01-21 15:44:08
 */
@Transactional(rollbackFor = Exception.class)
@Service("budgetService")
public class BudgetServiceImpl extends ServiceImpl<BudgetDao, Budget> implements BudgetService {

}

