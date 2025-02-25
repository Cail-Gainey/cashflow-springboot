package cashflow.service.impl;

import cashflow.common.RedisService;
import cashflow.dao.AccountStatusDao;
import cashflow.model.entity.AccountStatus;
import cashflow.model.vo.UserVo;
import cashflow.service.AccountStatusService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * (AccountStatus)表服务实现类
 *
 * @author Cail Gainey
 * @since 2025-02-18 16:16:11
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service("accountStatusService")
public class AccountStatusServiceImpl extends ServiceImpl<AccountStatusDao, AccountStatus> implements AccountStatusService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Resource
    private RedisService redisService;

    @Override
    public void saveStatus(@NonNull String token, @NonNull Integer status) {
        try {
            String userInfo = redisService.getUserInfoByToken(token);
            if (userInfo == null) {
                return;
            }
            UserVo user = MAPPER.readValue(userInfo, UserVo.class);

            AccountStatus accountStatus = AccountStatus.builder()
                    .userId(user.getId())
                    .status(status)
                    .statusCount(getCount(user.getId()) + 1)
                    .build();
            if (isBlackAccount(user.getId())) {
                return;
            }
            save(accountStatus);
            log.warn("检测到用户{}进行sql注入", user.getEmail());

            redisService.removeUser(token);
        } catch (JsonProcessingException e) {
            log.error("用户信息转换失败", e);
        }
    }


    public boolean isBlackAccount(Integer userId) {
        LambdaQueryWrapper<AccountStatus> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AccountStatus::getUserId, userId);
        AccountStatus accountStatus = getOne(lqw);
        return accountStatus != null;
    }

    private Integer getCount(Integer userId) {
        LambdaQueryWrapper<AccountStatus> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AccountStatus::getUserId, userId).select(AccountStatus::getStatusCount);
        AccountStatus accountStatus = getOne(lqw);
        if (accountStatus == null) {
            return 0;
        } else {
            return accountStatus.getStatusCount();
        }
    }
}

