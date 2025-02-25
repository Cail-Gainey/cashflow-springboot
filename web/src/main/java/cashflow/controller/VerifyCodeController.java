package cashflow.controller;

import cashflow.common.R;
import cashflow.enums.MessageEnums;
import cashflow.model.dto.VerifyDto;
import cashflow.service.VerifyService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码控制器，处理验证码的生成和验证
 *
 * @author Cail Gainey
 * @since 2025/1/25 15:01
 **/
@Slf4j
@RestController
@RequestMapping("/api/verify")
public class VerifyCodeController {
    @Resource
    private VerifyService verifyService;

    /**
     * 生成圆形验证码并存储到 Redis 中
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 包含验证码图片 URL 和 captchaKey 的响应
     */
    @GetMapping("/sendCode")
    public R<VerifyDto> getCircleCaptcha(HttpServletRequest request, HttpServletResponse response) {
        VerifyDto dto = verifyService.send();
        return R.success(dto);
    }

    /**
     * 验证用户输入的验证码
     *
     * @param dto 包含 captchaKey 和用户输入的验证码
     * @return 验证结果
     */
    @PostMapping("/verifyCode")
    public R<?> verifyCode(@RequestBody @NotNull VerifyDto dto) {
        try {
            boolean verified = verifyService.verify(dto);
            return verified ? R.success(null, MessageEnums.CODE_VERIFY_SUCCESS) : R.error(MessageEnums.CODE_VERIFY_ERROR);
        } catch (VerifyError e) {
            log.error(e.getMessage());
            return R.error(MessageEnums.SERVER_FAILURE);
        }
    }
}