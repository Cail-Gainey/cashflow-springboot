package cashflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账本表(Ledger)实体类
 *
 * @author Cail Gainey
 * @since 2025-01-21 20:08:11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ledger extends Model<Ledger> implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String name;

    private String remark;

    private String img;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @TableField(update = "now()")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
