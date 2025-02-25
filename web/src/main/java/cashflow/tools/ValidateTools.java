package cashflow.tools;


import cashflow.common.R;
import cashflow.enums.MessageEnums;
import cashflow.model.dto.FundDto;
import cashflow.model.dto.LedgerDto;
import cashflow.model.entity.User;
import io.micrometer.common.util.StringUtils;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Pattern;

/**
 * 字段判断
 *
 * @author Cail Gainey
 * @since 2025/1/26 18:09
 **/
public final class ValidateTools {
    public static R<?> validateUsers(@NonNull User user) {
        if (StringUtils.isBlank(user.getEmail()) || StringUtils.isBlank(user.getPassword())) {
            return R.error(MessageEnums.USER_NULL);
        }
        // 添加输入安全性检查
        if (!isValidEmail(user.getEmail())) {
            return R.error(MessageEnums.EMAIL_INVALID);
        }
        if (user.getPassword().length() < 6) {
            return R.error(MessageEnums.PASSWORD_LENGTH);
        }
        return null;
    }

    public static R<String> validateFile(@NonNull MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("请选择文件");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return R.error("只支持图片文件");
        }

        // 验证文件大小（例如最大2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            return R.error("文件大小不能超过2MB");
        }
        return null;
    }

    public static R<String> validateFundDto(@NonNull FundDto dto) {
        if ("默认资产".equals(dto.getName())) {
            return R.error("默认资产不支持修改名称");
        }
        if (dto.getRemark().length() > 20) {
            return R.error("备注字数超出上限");
        }
        return null;
    }

    public static R<String> validateLedgerDto(@NonNull LedgerDto dto) {
        if (dto.getImg() != null) {
            if (!dto.getImg().startsWith("/files/ledger/")) {
                return R.error("图片保存失败，请重新上传");
            }
        }

        if (dto.getRemark() != null) {
            if (dto.getRemark().length() > 20) {
                return R.error("备注字数超出上限");
            }
        }

        return null;
    }

    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (email != null && !email.isEmpty()) {
            return Pattern.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$", email);
        }
        return false;
    }

}