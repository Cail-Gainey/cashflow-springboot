package cashflow.service.impl;

import cashflow.enums.MessageEnums;
import cashflow.exception.VerifyException;
import cashflow.model.dto.VerifyDto;
import cashflow.service.VerifyService;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.img.ImgUtil.toBufferedImage;

/**
 * 验证码服务实现类
 *
 * @author Cail Gainey
 * @since 2025/1/25 15:01
 **/
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service("verifyService")
public class VerifyServiceImpl implements VerifyService {
    private static final long CAPTCHA_EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(1);
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final String IMAGE_FORMAT = "jpeg";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public VerifyDto send() {
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(150, 50, 4, 30);
        String code = RandomUtil.randomNumbers(4);
        Image image = circleCaptcha.createImage(code);
        String captchaKey = RandomUtil.randomString(16);

        redisTemplate.opsForValue().set(CAPTCHA_PREFIX + captchaKey, code, CAPTCHA_EXPIRATION_TIME, TimeUnit.MILLISECONDS);

        return VerifyDto.builder()
                .imageUrl(getImageUrl(image))
                .captchaKey(captchaKey)
                .build();
    }

    @Override
    public boolean verify(@NonNull VerifyDto dto) {
        String captchaKey = dto.getCaptchaKey();
        String inputCode = dto.getInputCode();

        if (captchaKey == null || inputCode == null) {
            throw new VerifyException(MessageEnums.CODE_VERIFY_INPUT_NULL.getMessage());
        }

        String storedCode = redisTemplate.opsForValue().get(CAPTCHA_PREFIX + captchaKey);
        if (storedCode == null) {
            throw new VerifyException(MessageEnums.CODE_VERIFY_NULL.getMessage());
        }

        return storedCode.equals(inputCode);
    }

    private @NotNull String getImageUrl(Image image) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            BufferedImage bufferedImage = toBufferedImage(image);
            ImageIO.write(bufferedImage, IMAGE_FORMAT, byteArrayOutputStream);
            return "data:image/" + IMAGE_FORMAT + ";base64," + Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            log.error("Error encoding captcha image", e);
            throw new VerifyException("Error generating captcha image");
        }
    }
}