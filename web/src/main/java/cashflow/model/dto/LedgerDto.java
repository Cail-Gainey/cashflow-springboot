package cashflow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:02
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LedgerDto {
    private Integer id;

    private String name;

    private String remark;

    private String img;
}
