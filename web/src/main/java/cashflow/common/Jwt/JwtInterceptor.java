package cashflow.common.Jwt;

import cashflow.common.R;
import cashflow.enums.MessageEnums;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.Optional;

/**
 * Jwt拦截器
 *
 * @author Cail Gainey
 * @since 2025/1/25 14:57
 **/

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "token";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader(TOKEN_HEADER);
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(objectMapper.writeValueAsString(R.error(MessageEnums.TOKEN_NULL)));
            return false;
        }

        Map<String, Claim> userData = JwtUtil.verifyToken(token);
        if (userData == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(objectMapper.writeValueAsString(R.error(MessageEnums.TOKEN_INVALID_ERROR)));
            return false;
        }

        Optional<Integer> id = Optional.ofNullable(userData.get("id")).map(Claim::asInt);
        Optional<String> username = Optional.ofNullable(userData.get("username")).map(Claim::asString);

        request.setAttribute("id", id.orElse(null));
        request.setAttribute("name", username.orElse(null));
        return true;
    }
}