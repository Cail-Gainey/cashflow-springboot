package cashflow.controller;


import cashflow.common.Jwt.JwtUtil;
import cashflow.common.R;
import cashflow.common.RedisService;
import cashflow.enums.MessageEnums;
import cashflow.model.dto.EmailDto;
import cashflow.service.EmailService;
import com.auth0.jwt.interfaces.Claim;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static cashflow.tools.ValidateTools.isValidEmail;

/**
 * @author Cail Gainey
 * @since 2025/1/25 14:59
 **/
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String TOKEN_HEADER = "token";
    @Resource
    private EmailService emailService;
    @Resource
    private RedisService redisService;

    @PostMapping("/verify")
    public R<?> verifyToken(@RequestHeader(TOKEN_HEADER) String token) {
        if (token == null || token.isEmpty()) {
            return R.error(MessageEnums.TOKEN_NULL);
        }
        String userInfo = redisService.getUserInfoByToken(token);
        if (userInfo == null || userInfo.isEmpty()) {
            return R.error(MessageEnums.TOKEN_VERITY_ERROR);
        }
        try {
            Map<String, Claim> claims = JwtUtil.verifyToken(token);
            return Optional.ofNullable(claims)
                    .map(c -> R.success(true, MessageEnums.TOKEN_VERIFY_SUCCESS))
                    .orElse(R.error(MessageEnums.TOKEN_VERITY_ERROR));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return R.error(MessageEnums.TOKEN_VERITY_ERROR);
        }
    }


    @PostMapping("/sendCode")
    public R<?> sendVerificationCode(@NotNull @RequestBody EmailDto emailDto) {
        if (!isValidEmail(emailDto.getEmail())) {
            return R.error(MessageEnums.EMAIL_INVALID);
        }

        emailService.sendVerificationCode(emailDto.getEmail(), "注册账号");
        return R.success(null, MessageEnums.EMAIL_SEND_SUCCESS);
    }

    @PostMapping("/forget")
    public R<String> forgetPwd(@RequestBody @NotNull EmailDto emailDto) {
        if (!isValidEmail(emailDto.getEmail())) {
            return R.error(MessageEnums.EMAIL_INVALID);
        }

        emailService.sendVerificationCode(emailDto.getEmail(), "忘记密码");
        return R.success(null, MessageEnums.EMAIL_SEND_SUCCESS);
    }

    @PostMapping("/acceptCode")
    public R<?> verifyCode(@RequestBody @NotNull EmailDto emailDto) {
        boolean isValid = emailService.verifyCode(emailDto.getEmail(), emailDto.getCode());
        if (isValid) {
            return R.success(null, MessageEnums.EMAIL_VERIFY_SUCCESS);
        }
        return R.error(MessageEnums.EMAIL_VERIFY_ERROR);
    }


}