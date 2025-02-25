package cashflow.service.impl;

import cashflow.dao.LedgerDao;
import cashflow.model.entity.Ledger;
import cashflow.model.vo.LedgerVo;
import cashflow.service.FileService;
import cashflow.service.LedgerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 账本表(Ledger)表服务实现类
 *
 * @author Cail Gainey
 * @since 2025-01-21 20:08:11
 */
@Transactional(rollbackFor = Exception.class)
@Service("ledgerService")
public class LedgerServiceImpl extends ServiceImpl<LedgerDao, Ledger> implements LedgerService {
    @Resource
    private FileService fileService;
    @Resource
    private LedgerDao ledgerDao;

    @Override
    public List<LedgerVo> listAllById(Integer userId) {
        LambdaQueryWrapper<Ledger> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Ledger::getUserId, userId);
        List<Ledger> list = this.list(lqw);


        return list.stream().map(ledger -> LedgerVo.builder()
                        .id(ledger.getId())
                        .name(ledger.getName())
                        .remark(ledger.getRemark())
                        .img(fileService.getFullFileUrl(ledger.getImg()))
                        .build())
                .toList();
    }

    @Override
    public void addDefaultLedger(Integer userId) {
        Ledger defaultLedger = Ledger.builder()
                .userId(userId)
                .build();
        save(defaultLedger);
    }

    @Override
    public void removeLedger(Integer id) {
        LambdaQueryWrapper<Ledger> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Ledger::getId, id);
        ledgerDao.delete(lqw);
    }
}

