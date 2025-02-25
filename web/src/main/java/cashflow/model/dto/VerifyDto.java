package cashflow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码
 *
 * @author Cail Gainey
 * @since 2025/1/25 15:01
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyDto {
    private String imageUrl;

    private String captchaKey;

    private String inputCode;
}
