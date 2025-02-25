package cashflow.dao;

import cashflow.model.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.NonNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 分类表(Category)表数据库访问层
 *
 * @author Cail Gainey
 * @since 2025-01-21 14:20:47
 */
@Mapper
public interface CategoryDao extends BaseMapper<Category> {
    @Select("select name from category where id = #{id} limit 1")
    String getName(@NonNull Integer id);

    @Select("select type from category where parent_id = #{parentId} limit 1")
    Integer getParentIdType(@NonNull Integer parentId);

    @Select("select type from category where id = #{categoryId} limit 1")
    Integer getCategoryType(@NonNull Integer categoryId);
}

