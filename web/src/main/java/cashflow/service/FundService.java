package cashflow.service;

import cashflow.model.dto.FundDto;
import cashflow.model.entity.Fund;
import cashflow.model.vo.FundVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 资金账户表(Fund)表服务接口
 *
 * @author Cail Gainey
 * @since 2025-01-21 19:17:56
 */
public interface FundService extends IService<Fund> {
    List<FundVo> listAllById(Integer userId);

    void addDefaultFund(Integer userId);

    void updateByDto(FundDto dto, Integer id);

    Integer getDefaultId(Integer userId);
}

