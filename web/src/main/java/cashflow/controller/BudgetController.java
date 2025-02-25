package cashflow.controller;


import cashflow.service.BudgetService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 预算表(Budget)表控制层
 *
 * @author Cail Gainey
 * @since 2025-01-21 15:44:08
 */
@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Resource
    private BudgetService budgetService;

}

