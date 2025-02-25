package cashflow.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:02
 **/
@Data
@NoArgsConstructor
public class EmailDto {
    private String email;
    private String code;
}