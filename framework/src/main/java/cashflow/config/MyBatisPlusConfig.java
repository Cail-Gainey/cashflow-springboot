package cashflow.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:04
 **/
@Slf4j
@Configuration
public class MyBatisPlusConfig {

    // 通过 @Value 注解从配置文件中读取 DbType，默认值为 MYSQL
    @Value("${database.type:mysql}")
    private String databaseType;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        try {
            // 将字符串转换为 DbType 枚举
            DbType dbType = DbType.valueOf(databaseType.toUpperCase());
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
        } catch (IllegalArgumentException e) {
            // 日志输出异常信息，并记录错误的数据库类型
            log.error("Invalid database type: {}.", databaseType, e);
            // 可选择抛出自定义异常或使用默认数据库类型
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        }

        return interceptor;
    }
}
