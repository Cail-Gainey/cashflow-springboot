package cashflow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:02
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FundDto {
    private String name;

    private BigDecimal balance;

    private String remark;

    private Integer isCount;
}
