package cashflow.controller;


import cashflow.common.R;
import cashflow.model.vo.FundDetailVo;
import cashflow.service.FundDetailService;
import lombok.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 资金明细表(FundDetail)表控制层
 *
 * @author Cail Gainey
 * @since 2025-01-21 19:18:17
 */
@RestController
@RequestMapping("/api/detail")
public class FundDetailController {

    @Resource
    private FundDetailService detailService;

    @GetMapping("/{userId}/{fundId}")
    public R<List<FundDetailVo>> listByFundId(@NonNull @PathVariable Integer userId, @NonNull @PathVariable Integer fundId) {
        List<FundDetailVo> list = detailService.listByFundId(userId, fundId);
        return R.success(list);
    }

}

