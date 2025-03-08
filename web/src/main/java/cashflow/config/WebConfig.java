package cashflow.config;

import cashflow.common.Jwt.JwtInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author Cail Gainey
 * @since 2025/1/25 14:59
 **/
@Component
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //  设置允许跨域的路径
                .allowedOriginPatterns("*")
                //是否允许cookie
                .allowCredentials(true)
                //设置允许的请求方式
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                //设置允许的header
                .allowedHeaders("*")
                //跨域允许时间
                .maxAge(3600);
    }
     @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/public/*")
                .excludePathPatterns("/api/verify/*")
                .excludePathPatterns("/files/{folder}/{filename}");
    }
}
