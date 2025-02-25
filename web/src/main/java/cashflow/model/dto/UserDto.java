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
public class UserDto {
    private String username;

    private String password;

    private String phone;

    private Integer sex;

    private String avatar;
}
