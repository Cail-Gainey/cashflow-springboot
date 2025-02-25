package cashflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 资金账户表(Fund)实体类
 *
 * @author Cail Gainey
 * @since 2025-01-21 19:17:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fund extends Model<Fund> implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String name;

    private BigDecimal balance;

    private String remark;

    private Integer isCount = 1;
    @Builder.Default
    private LocalDate createdAt = LocalDate.now();
    @TableField(update = "now()")
    private LocalDate updatedAt = LocalDate.now();
}
