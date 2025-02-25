package cashflow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:00
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String email;

    private String password;

    private boolean rememberMe;
}
