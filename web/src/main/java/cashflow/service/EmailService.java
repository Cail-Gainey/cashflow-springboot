package cashflow.service;


/**
 * 邮箱服务接口
 *
 * @author Cail Gainey
 * @since 2025/1/26 18:10
 **/
public interface EmailService {
    void sendVerificationCode(String toEmail, String span);

    boolean verifyCode(String email, String inputCode);
}