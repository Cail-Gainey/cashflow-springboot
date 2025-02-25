package cashflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 分类表(Category)实体类
 *
 * @author Cail Gainey
 * @since 2025-01-21 14:20:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category extends Model<Category> implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String name;

    private Integer type;

    private Integer parentId;
    @Builder.Default
    private LocalDate createdAt = LocalDate.now();
    @TableField(update = "now()")
    private LocalDate updatedAt = LocalDate.now();
}
