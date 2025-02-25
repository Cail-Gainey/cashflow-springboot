package cashflow.service.impl;


import cashflow.service.EmailService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import static cashflow.tools.ValidateTools.isValidEmail;

/**
 * 邮箱服务实现类
 *
 * @author Cail Gainey
 * @since 2025-1-26 18:12
 **/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class EmailServiceImpl implements EmailService {

    private static final String VERIFICATION_CODE_PREFIX = "mail-code:";  // Redis key 前缀
    private static final int CODE_LENGTH = 6;  // 验证码长度
    private static final String EMAIL_SUBJECT = "邮箱验证码";  // 邮件主题
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Value("${spring.mail.username}")
    private String former;

    /**
     * 发送验证码到指定邮箱
     */
    @Override
    public void sendVerificationCode(String toEmail, String span) {
        if (!isValidEmail(toEmail)) {
            log.warn("无效邮箱: {}", toEmail);  // 输出无效邮箱地址
            return;
        }

        // 生成验证码并保存到 Redis
        String verificationCode = generateVerificationCode();
        redisTemplate.opsForValue().set(VERIFICATION_CODE_PREFIX + toEmail, verificationCode, 5, TimeUnit.MINUTES);

        // 发送邮件
        sendEmail(toEmail, verificationCode, span);
    }

    /**
     * 生成随机验证码
     */
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt((int) Math.pow(10, CODE_LENGTH));  // 生成随机验证码
        return String.format("%06d", code);  // 格式化为6位数
    }

    /**
     * 发送邮件
     */
    private void sendEmail(String toEmail, String verificationCode, String span) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(former);  // 设置发件人
            helper.setTo(toEmail);  // 设置收件人
            helper.setSubject(EMAIL_SUBJECT);  // 设置邮件主题

            // 使用 Thymeleaf 渲染模板并替换验证码占位符
            Context context = new Context();
            context.setVariable("verifyCode", verificationCode);  // 设置动态变量
            context.setVariable("span", span);
            // 使用 Thymeleaf 渲染模板
            String processedEmailContent = templateEngine.process("email", context);  // 传入模板名

            helper.setText(processedEmailContent, true);  // 设置邮件内容为 HTML

            // 发送邮件
            mailSender.send(message);
            log.info("已发送验证码到: {}, 验证码:{}", toEmail, context.getVariable("verifyCode"));
        } catch (MessagingException e) {
            log.error("邮件发送失败: {}", e.getMessage());
        }
    }

    /**
     * 验证输入的验证码是否与 Redis 中存储的验证码匹配
     */
    @Override
    public boolean verifyCode(String email, String inputCode) {
        String cachedCode = redisTemplate.opsForValue().get(VERIFICATION_CODE_PREFIX + email);
        return cachedCode != null && cachedCode.equals(inputCode);  // 返回验证结果
    }
}
