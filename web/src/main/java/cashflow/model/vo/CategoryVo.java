package cashflow.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:03
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryVo {

    private Integer id;

    private String name;

    private Integer type;

    private Integer parentId;

    private List<CategoryVo> children;
}