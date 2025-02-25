package cashflow.model.dto;

import lombok.Data;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:02
 **/
@Data
public class RegisterDto {
    private String email;
    private String password;
    private String username;
    private Integer sex;
    private String avatar;
}
