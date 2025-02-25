package cashflow.exception;


import cashflow.common.R;
import cashflow.enums.MessageEnums;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:05
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {
    //拦截所有的异常处理
    @ExceptionHandler
    public R<String> doException(@NotNull Exception e) {
        e.printStackTrace();
        return R.error("服务器故障，请稍后再试");
    }

    @ExceptionHandler(TokenVerificationException.class)
    public R<String> tokenException(@NotNull TokenVerificationException e) {
        log.error(e.getMessage());
        return R.error(MessageEnums.TOKEN_VERITY_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public R<String> noResourceFound(@NotNull NoResourceFoundException e) {
        log.error("No static resource:{}", e.getResourcePath());
        return R.error("获取资源失败");
    }

    @ExceptionHandler(MailSendException.class)
    public R<String> sendException(@NotNull MailSendException e) {
        log.error(e.getMessage());
        return R.error(MessageEnums.EMAIL_ADDRESS_INVALID);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public R<String> noHeader(@NotNull MissingRequestHeaderException e) {
        log.error("缺少请求头：{}", e.getHeaderName());
        return R.error(MessageEnums.HEADER_MISS + e.getHeaderName());
    }
}