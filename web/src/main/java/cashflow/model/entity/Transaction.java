package cashflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账目表(Transaction)实体类
 *
 * @author Cail Gainey
 * @since 2025-01-21 20:23:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends Model<Transaction> implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer ledgerId;

    private Integer fundId;

    private Integer type;

    private Integer categoryId;

    private BigDecimal amount;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Builder.Default
    private LocalDateTime time = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @TableField(update = "now()")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
