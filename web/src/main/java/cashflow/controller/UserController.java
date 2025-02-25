package cashflow.controller;

import cashflow.common.R;
import cashflow.common.RedisService;
import cashflow.enums.MessageEnums;
import cashflow.model.dto.UserDto;
import cashflow.model.entity.User;
import cashflow.model.vo.UserVo;
import cashflow.service.FileService;
import cashflow.service.UserService;
import cashflow.tools.ValidateTools;
import cashflow.utils.AESUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:01
 **/
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final String TOKEN_HEADER = "token";
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;
    @Resource
    private RedisService redisService;

    @PostMapping("/avatar")
    public R<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            R<String> validateResult = ValidateTools.validateFile(file);
            if (validateResult != null) {
                return validateResult;
            }

            String relativePath = fileService.saveFile(file, "avatar");
            return R.success(relativePath);
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return R.error("头像上传失败");
        }
    }

    @GetMapping("/{id}")
    public R<?> getById(@PathVariable("id") @NonNull Integer id) {
        UserVo dbUser = userService.getByUserId(id);
        if (dbUser != null) {
            // 添加服务器地址到头像路径
            dbUser.setAvatar(fileService.getFullFileUrl(dbUser.getAvatar()));
        }
        return R.success(dbUser);
    }

    @PutMapping("/{userId}")
    public R<?> updateUsers(@NonNull @RequestBody UserDto dto, @NonNull @RequestHeader(TOKEN_HEADER) String token, @NonNull @PathVariable Integer userId) {
        try {
            LambdaUpdateWrapper<User> luw = new LambdaUpdateWrapper<>();
            luw.eq(User::getId, userId);
            luw.set(User::getUsername, dto.getUsername());
            luw.set(User::getAvatar, fileService.processFilePath(dto.getAvatar()));
            luw.set(User::getSex, dto.getSex());
            if (dto.getPhone() != null) {
                luw.set(User::getPhone, dto.getPhone());
            }
            if (dto.getPassword() != null) {
                String encryptPassword = AESUtil.encrypt(dto.getPassword());
                luw.set(User::getPassword, encryptPassword);
            }

            boolean isUpdated = userService.update(luw);
            redisService.updateUserInfo(token, userId);
            return isUpdated ? R.success() : R.error();
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.error(MessageEnums.SERVER_FAILURE);
        }
    }

    @PutMapping("/forget")
    public R<String> forgetPwd(@RequestBody @NotNull User user) {
        if (user.getEmail() == null) {
            return R.error(MessageEnums.NULL);
        }

        try {
            String encryptedPassword = AESUtil.encrypt(user.getPassword());
            user.setPassword(encryptedPassword);
            LambdaUpdateWrapper<User> lqw = new LambdaUpdateWrapper<>();
            lqw.eq(User::getEmail, user.getEmail())
                    .set(User::getPassword, encryptedPassword);

            boolean updated = userService.update(null, lqw);
            return updated ? R.success() : R.error();
        } catch (Exception e) {
            log.error("密码重置失败", e);
            return R.error(MessageEnums.PASSWORD_ENCRYPTION_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public R<?> removeUsers(@PathVariable Integer id) {
        if (id == null) {
            return R.error(MessageEnums.USER_ID_NULL);
        }
        boolean isRemoved = userService.removeById(id);
        return isRemoved ? R.success("删除成功") : R.error("删除失败");
    }
}
