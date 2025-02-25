package cashflow.controller;


import cashflow.common.R;
import cashflow.enums.MessageEnums;
import cashflow.exception.CategoryException;
import cashflow.model.dto.CategoryDto;
import cashflow.model.entity.Category;
import cashflow.model.vo.CategoryVo;
import cashflow.service.CategoryService;
import cashflow.service.FundDetailService;
import cashflow.service.TransactionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类表(Category)表控制层
 *
 * @author Cail Gainey
 * @since 2025-01-21 14:20:47
 */
@Slf4j
@RestController
@RequestMapping("/api/category/{userId}")
public class CategoryController {
    @Resource
    private CategoryService categoryService;
    @Resource
    private TransactionService transactionService;
    @Resource
    private FundDetailService detailService;

    @GetMapping()
    public R<List<CategoryVo>> listById(@PathVariable @NonNull Integer userId) {
        List<CategoryVo> list = categoryService.listAllById(userId);
        return R.success(list);
    }

    @PostMapping()
    private R<String> save(@RequestBody @NonNull CategoryDto dto, @PathVariable @NonNull Integer userId) {
        try {
            LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Category::getName, dto.getName());

            if (categoryService.exists(lqw)) {
                return R.error(MessageEnums.CATEGORY_EXIST);
            }

            // 检查分类层级限制
            categoryService.checkParent(dto.getType(), dto.getParentId());

            Category category = Category.builder()
                    .userId(userId)
                    .name(dto.getName())
                    .type(dto.getType())
                    .parentId(dto.getParentId())
                    .build();

            boolean saved = categoryService.save(category);
            return saved ? R.success() : R.error();
        } catch (CategoryException e) {
            log.error("添加分类失败", e);
            return R.error(e.getMessage());
        }
    }

    // 迁移分类
    @PostMapping("/{oldId}/{newId}")
    public R<String> transfer(@NonNull @PathVariable Integer userId, @NonNull @PathVariable Integer oldId, @NonNull @PathVariable Integer newId) {
        try {
            LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Category::getId, newId).eq(Category::getUserId, userId);

            if (!categoryService.exists(lqw)) {
                return R.error(MessageEnums.CATEGORY_NOT_EXIST);
            }

            transactionService.transferCategory(oldId, newId);
            detailService.transferCategory(oldId, newId);
            return R.success();
        } catch (Exception e) {
            log.error("{}迁移到{}失败", oldId, newId);
            return R.error(MessageEnums.SERVER_FAILURE);
        }
    }

    /*
     * 修改分类名称
     * */
    @PutMapping()
    public R<String> update(@NonNull @RequestBody CategoryDto dto, @NonNull @PathVariable Integer userId) {
        try {
            LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Category::getId, dto.getId()).eq(Category::getUserId, userId);

            if (!categoryService.exists(lqw)) {
                return R.error(MessageEnums.CATEGORY_NOT_EXIST);
            }

            LambdaUpdateWrapper<Category> luw = new LambdaUpdateWrapper<>();
            luw.eq(Category::getId, dto.getId()).eq(Category::getUserId, userId)
                    .set(Category::getName, dto.getName());

            boolean updated = categoryService.update(luw);
            return updated ? R.success() : R.error();
        } catch (Exception e) {
            log.error("修改分类失败");
            return R.error(MessageEnums.SERVER_FAILURE);
        }
    }

    //改为一级分类
    @PutMapping("/{id}")
    public R<String> toParent(@NonNull @PathVariable Integer userId, @NonNull @PathVariable Integer id) {
        try {
            LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Category::getId, id).eq(Category::getUserId, userId);

            if (!categoryService.exists(lqw)) {
                return R.error(MessageEnums.CATEGORY_NOT_EXIST);
            }

            LambdaUpdateWrapper<Category> luw = new LambdaUpdateWrapper<>();
            luw.eq(Category::getId, id).eq(Category::getUserId, userId).set(Category::getParentId, null);
            categoryService.update(luw);
            return R.success();
        } catch (Exception e) {
            log.error("{}改为一级分类失败", id);
            return R.error(MessageEnums.SERVER_FAILURE);
        }

    }

    /*
     * 改为二级分类
     * */
    @PutMapping("/{id}/{parentId}")
    private R<String> toChildren(@NonNull @PathVariable Integer userId, @NonNull @PathVariable Integer id, @NonNull @PathVariable Integer parentId) {
        try {
            LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Category::getId, id).eq(Category::getUserId, userId);

            if (!categoryService.exists(lqw)) {
                return R.error(MessageEnums.CATEGORY_NOT_EXIST);
            }

            if (!categoryService.isParent(id)) {
                return R.error(MessageEnums.CATEGORY_CANT_UPDATE_TO_PARENT);
            }

            Integer children = categoryService.countChildren(id);
            if (children > 0) {
                return R.error(MessageEnums.CATEGORY_CANT_UPDATE_TO_CHILDREN);
            }

            LambdaUpdateWrapper<Category> luw = new LambdaUpdateWrapper<>();
            luw.eq(Category::getId, id).eq(Category::getUserId, userId)
                    .set(Category::getParentId, parentId);

            categoryService.update(luw);
            return R.success();
        } catch (Exception e) {
            log.error("{}改为{}的二级分类失败", id, parentId);
            return R.error(MessageEnums.SERVER_FAILURE);
        }
    }

    @DeleteMapping("/{id}")
    public R<String> remove(@NonNull @PathVariable Integer userId, @NonNull @PathVariable Integer id) {
        try {
            LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Category::getId, id).eq(Category::getUserId, userId);

            if (!categoryService.exists(lqw)) {
                return R.error(MessageEnums.CATEGORY_NOT_EXIST);
            }

            Category category = categoryService.getById(id);
            if (category.getParentId() == null) {
                Integer children = categoryService.countChildren(id);
                if (children > 0) {
                    return R.error(MessageEnums.CATEGORY_PARENT_CANT_DELETE);
                }
            } else {
                Integer count = transactionService.countCategory(id);
                if (count > 0) {
                    return R.error(MessageEnums.CATEGORY_CHILDREN_CANT_DELETE);
                }
            }

            categoryService.removeById(id);
            return R.success();
        } catch (Exception e) {
            log.error("删除分类失败");
            return R.error(MessageEnums.SERVER_FAILURE);
        }
    }

}

