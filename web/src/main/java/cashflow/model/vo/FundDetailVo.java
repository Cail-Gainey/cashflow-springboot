package cashflow.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资金明细表(FundDetail)视图实体类
 *
 * @author Cail Gainey
 * @since 2025-01-21 19:18:17
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FundDetailVo {
    private Integer id;

    private Integer categoryId;

    private Integer ledgerId;

    private Integer fundId;

    private String fundName;

    private String categoryName;

    private Integer type;

    private BigDecimal amount;

    private String description;

    private LocalDateTime time;
}