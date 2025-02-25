package cashflow.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:03
 **/

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LedgerVo {
    private Integer id;

    private String name;

    private String remark;

    private String img;
}
