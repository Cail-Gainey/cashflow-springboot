package cashflow.service.impl;

import cashflow.dao.CategoryDao;
import cashflow.enums.MessageEnums;
import cashflow.exception.CategoryException;
import cashflow.model.entity.Category;
import cashflow.model.vo.CategoryVo;
import cashflow.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author Cail Gainey
 * @since 2025/1/26 18:12
 **/
@Transactional(rollbackFor = Exception.class)
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {
    @Resource
    private CategoryDao categoryDao;

    @Override
    public List<CategoryVo> listAllById(@NonNull Integer userId) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getUserId, userId);

        List<Category> categoryList = this.list(lqw);
        Map<Integer, List<Category>> categoryMap = categoryList.stream()
                .collect(Collectors.groupingBy(category -> Optional.ofNullable(category.getParentId()).orElse(-1)));

        return categoryMap.getOrDefault(-1, List.of()).stream()
                .map(category -> {
                    CategoryVo categoryVo = buildCategoryVo(category, categoryMap);
                    // 确保 children 为 null 时返回空数组
                    if (categoryVo.getChildren() == null) {
                        categoryVo.setChildren(new ArrayList<>());
                    }
                    return categoryVo;
                })
                .toList();
    }

    @Override
    public void checkParent(@NonNull Integer type, @Nullable Integer parentId) {
        if (parentId == null) {
            return;
        }

        Category category = this.getById(parentId);
        if (category == null) {
            throw new CategoryException(MessageEnums.CATEGORY_ParentIdNotExist.getMessage());
        }

        if (!Objects.equals(category.getType(), type)) {
            throw new CategoryException(MessageEnums.CATEGORY_TYPE_ERROR.getMessage());
        }
    }

    @Override
    public void checkCategoryType(@NonNull Integer type, @NonNull Integer categoryId) {
        Integer categoryType = categoryDao.getCategoryType(categoryId);
        if (!Objects.equals(categoryType, type)) {
            throw new CategoryException(MessageEnums.CATEGORY_TYPE_ERROR.getMessage());
        }
    }

    @Override
    public Integer countChildren(Integer id) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getParentId, id);

        return Math.toIntExact(categoryDao.selectCount(lqw));
    }

    @Override
    public boolean isParent(Integer id) {
        Category category = this.getById(id);
        return category.getParentId() == null;
    }

    private CategoryVo buildCategoryVo(@NonNull Category category, @NonNull Map<Integer, List<Category>> categoryMap) {
        CategoryVo categoryVo = CategoryVo.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .parentId(category.getParentId())
                .build();

        if (categoryMap.containsKey(category.getId())) {
            List<CategoryVo> children = categoryMap.get(category.getId()).stream()
                    .map(child -> buildCategoryVo(child, categoryMap))
                    .toList();
            categoryVo.setChildren(children);
        }

        return categoryVo;
    }
}