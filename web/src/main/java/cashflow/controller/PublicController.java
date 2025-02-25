package cashflow.controller;

import cashflow.common.Jwt.JwtUtil;
import cashflow.common.R;
import cashflow.common.RedisService;
import cashflow.enums.MessageEnums;
import cashflow.exception.AccountStatusException;
import cashflow.model.dto.LoginDto;
import cashflow.model.dto.RegisterDto;
import cashflow.model.entity.User;
import cashflow.model.vo.UserVo;
import cashflow.service.FileService;
import cashflow.service.FundService;
import cashflow.service.LedgerService;
import cashflow.service.UserService;
import cashflow.tools.ValidateTools;
import cashflow.utils.AESUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 登录、退出控制层
 *
 * @author Cail Gainey
 * @since 2025/1/25 15:00
 **/
@Slf4j
@RestController()
@RequestMapping("/api/public")
public class PublicController {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String TOKEN_HEADER = "token";

    @Resource
    private UserService userService;
    @Resource
    private FundService fundService;
    @Resource
    private LedgerService ledgerService;
    @Resource
    private RedisService redisService;
    @Resource
    private FileService fileService;


    @PostMapping("/login")
    public R<?> login(@RequestBody @NonNull LoginDto dto) throws JsonProcessingException {
        try {
            if (!ValidateTools.isValidEmail(dto.getEmail())) {
                return R.error(MessageEnums.EMAIL_INVALID);
            }

            User user = User.builder()
                    .email(dto.getEmail())
                    .password(dto.getPassword())
                    .build();

            R<?> validationResult = ValidateTools.validateUsers(user);
            if (validationResult != null) {
                return validationResult;
            }

            UserVo loggedInUser = userService.login(dto.getEmail(), dto.getPassword());
            if (loggedInUser == null) {
                return R.error(MessageEnums.USER_NOT_EXIST);
            }

            Optional.ofNullable(loggedInUser.getAvatar())
                    .filter(avatar -> !avatar.isEmpty())
                    .ifPresent(avatar -> loggedInUser.setAvatar(fileService.getFullFileUrl(avatar)));

            String token = JwtUtil.createToken(user, dto.isRememberMe());
            String jsonUsers = MAPPER.writeValueAsString(loggedInUser);
            redisService.saveUserToken(token, jsonUsers, dto.isRememberMe());

            return R.success(token, MessageEnums.LOGIN_SUCCESS);
        } catch (AccountStatusException e) {
            return R.error(e.getMessage());
        }

    }

    @PostMapping("/logout")
    public R<?> logout(@RequestHeader(TOKEN_HEADER) String token) {
        Optional.ofNullable(token)
                .orElseThrow(() -> new RuntimeException(MessageEnums.TOKEN_VERITY_ERROR.getMessage()));
        redisService.removeUser(token);
        return R.success(MessageEnums.LOGOUT_SUCCESS);
    }

    @PostMapping("/register")
    public R<?> register(@RequestBody @NonNull RegisterDto dto) {
        User user = User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .username(dto.getUsername())
                .sex(dto.getSex())
                .avatar(fileService.processFilePath(dto.getAvatar()))
                .build();

        R<?> validationResult = ValidateTools.validateUsers(user);
        if (validationResult != null) {
            return validationResult;
        }

        if (userService.countUsers(dto.getEmail()) > 0) {
            return R.error(MessageEnums.USER_EXIST);
        }

        try {
            user.setPassword(AESUtil.encrypt(dto.getPassword()));
            if (userService.save(user)) {
                fundService.addDefaultFund(user.getId());
                ledgerService.addDefaultLedger(user.getId());
                return R.success();
            }
            return R.error(MessageEnums.REGISTRATION_ERROR);
        } catch (Exception e) {
            log.error(MessageEnums.PASSWORD_ENCRYPTION_ERROR.getMessage(), e);
            return R.error(MessageEnums.SERVER_FAILURE);
        }
    }
}