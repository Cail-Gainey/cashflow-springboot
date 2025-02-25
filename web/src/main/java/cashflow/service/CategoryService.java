package cashflow.service;

import cashflow.model.entity.Category;
import cashflow.model.vo.CategoryVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 分类表(Category)表服务接口
 *
 * @author Cail Gainey
 * @since 2025-01-21 14:20:47
 */
public interface CategoryService extends IService<Category> {
    List<CategoryVo> listAllById(Integer userId);

    void checkParent(Integer type, Integer parentId);

    void checkCategoryType(Integer type, Integer categoryId);

    Integer countChildren(Integer id);

    boolean isParent(Integer id);
}