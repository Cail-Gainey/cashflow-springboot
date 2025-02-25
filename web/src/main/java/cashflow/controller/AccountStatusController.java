package cashflow.controller;


import cashflow.service.AccountStatusService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * (AccountStatus)表控制层
 *
 * @author Cail Gainey
 * @since 2025-02-18 16:16:10
 */
@RestController
@RequestMapping("accountStatus")
public class AccountStatusController {

    @Resource
    private AccountStatusService accountStatusService;

}

