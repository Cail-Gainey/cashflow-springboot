package cashflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资金明细表(FundDetail)实体类
 *
 * @author Cail Gainey
 * @since 2025-01-21 19:18:17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FundDetail extends Model<FundDetail> implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer transactionId;

    private Integer fundId;

    private Integer ledgerId;

    private Integer categoryId;

    private Integer type;

    private BigDecimal amount;

    private BigDecimal oldBalance;

    private BigDecimal newBalance;

    private String description;

    @Builder.Default
    private LocalDateTime time = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}