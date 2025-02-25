package cashflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预算表(Budget)实体类
 *
 * @author Cail Gainey
 * @since 2025-01-21 15:44:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Budget extends Model<Budget> implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer categoryId;

    private BigDecimal amount;

    private String month;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @TableField(update = "now()")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
