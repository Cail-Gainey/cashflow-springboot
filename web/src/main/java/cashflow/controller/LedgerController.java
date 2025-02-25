package cashflow.controller;

import cashflow.common.R;
import cashflow.enums.MessageEnums;
import cashflow.model.dto.LedgerDto;
import cashflow.model.dto.VerifyDto;
import cashflow.model.entity.Ledger;
import cashflow.model.vo.LedgerVo;
import cashflow.service.*;
import cashflow.tools.ValidateTools;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 账本表(Ledger)表控制层
 *
 * @author Cail Gainey
 * @since 2025-01-21 20:08:11
 */
@Slf4j
@RestController
@RequestMapping("/api/ledger")
public class LedgerController {
    @Resource
    private LedgerService ledgerService;
    @Resource
    private FileService fileService;
    @Resource
    private TransactionService transactionService;
    @Resource
    private FundDetailService detailService;
    @Resource
    private VerifyService verifyService;

    @GetMapping("/{userId}")
    public R<List<LedgerVo>> listAllById(@PathVariable @NonNull Integer userId) {
        List<LedgerVo> list = ledgerService.listAllById(userId);
        return R.success(list);
    }

    @PostMapping("/{userId}")
    public R<String> save(@RequestBody @NonNull LedgerDto dto, @PathVariable @NonNull Integer userId) {
        R<String> validateResult = ValidateTools.validateLedgerDto(dto);
        if (validateResult != null) {
            return validateResult;
        }

        if (isLedgerExist(dto.getName(), userId)) {
            fileService.removeFile(dto.getImg());
            return R.error(MessageEnums.LEDGER_EXIST);
        }

        Ledger ledger = buildLedger(dto, userId);
        boolean saved = ledgerService.save(ledger);
        if (saved) {
            return R.success();
        } else {
            fileService.removeFile(dto.getImg());
            return R.error(MessageEnums.SERVER_FAILURE);
        }
    }

    @PostMapping("/img")
    public R<String> uploadImg(@RequestParam("file") MultipartFile file) {
        R<String> validateResult = ValidateTools.validateFile(file);
        if (validateResult != null) {
            return validateResult;
        }

        String imgPath = fileService.saveFile(file, "ledger");
        return R.success(imgPath);
    }

    @PutMapping("/{userId}")
    public R<String> update(@RequestBody @NonNull LedgerDto dto, @PathVariable @NonNull Integer userId) {
        Ledger dbLedger = ledgerService.getById(dto.getId());
        if ("初始账本".equals(dbLedger.getName())) {
            dto.setName("初始账本");
        }
        R<String> validateResult = ValidateTools.validateLedgerDto(dto);
        if (validateResult != null) {
            return validateResult;
        }

        if (!isLedgerExist(dto.getName(), userId)) {
            fileService.removeFile(dto.getImg());
            return R.error(MessageEnums.LEDGER_NOT_EXIST);
        }

        Ledger ledger = buildLedger(dto, userId);
        LambdaQueryWrapper<Ledger> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Ledger::getUserId, userId).eq(Ledger::getId, dto.getId());
        boolean updated = ledgerService.update(ledger, lqw);
        if (updated) {
            return R.success();
        } else {
            fileService.removeFile(dto.getImg());
            return R.error(MessageEnums.SERVER_FAILURE);
        }
    }

    @PutMapping("/clean/{userId}/{id}")
    public R<String> clean(@RequestBody @NonNull VerifyDto dto, @PathVariable @NonNull Integer userId, @PathVariable @NonNull Integer id) {
        return handleVerification(dto, userId, id, () -> {
            transactionService.cleanByLedgerId(userId, id);
            detailService.cleanByLedgerId(id);
        });
    }

    @Transactional
    @PutMapping("/remove/{userId}/{id}")
    public R<String> remove(@RequestBody @NonNull VerifyDto dto, @PathVariable @NonNull Integer userId, @PathVariable @NonNull Integer id) {
        return handleVerification(dto, userId, id, () -> {
            transactionService.cleanByLedgerId(userId, id);
            detailService.cleanByLedgerId(id);
            ledgerService.removeLedger(id);
        });
    }

    @Transactional
    @PutMapping("/shift/{userId}/{oldId}/{newId}")
    public R<String> shift(@PathVariable @NonNull Integer userId, @PathVariable @NonNull Integer oldId, @PathVariable @NonNull Integer newId) {
        try {
            transactionService.shiftByLedgerId(userId, oldId, newId);
            detailService.shiftByLedgerId(oldId, newId);
            return R.success();
        } catch (Exception e) {
            log.error("账本移动失败", e);
            return R.error(e.getMessage());
        }
    }

    private R<String> handleVerification(VerifyDto dto, Integer userId, Integer id, Runnable action) {
        try {
            if (verifyService.verify(dto)) {
                action.run();
                return R.success();
            } else {
                return R.error(MessageEnums.CODE_VERIFY_ERROR);
            }
        } catch (Exception e) {
            log.error("操作失败", e);
            return R.error(MessageEnums.SERVER_FAILURE);
        }
    }

    private boolean isLedgerExist(String ledgerName, Integer userId) {
        LambdaQueryWrapper<Ledger> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Ledger::getName, ledgerName).eq(Ledger::getUserId, userId);
        return ledgerService.exists(lqw);
    }

    private Ledger buildLedger(@NotNull LedgerDto dto, Integer userId) {
        return Ledger.builder()
                .userId(userId)
                .name(dto.getName())
                .remark(dto.getRemark())
                .img(dto.getImg())
                .build();
    }
}