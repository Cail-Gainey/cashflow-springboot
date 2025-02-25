package cashflow.filter;


import cashflow.enums.AccountStatusEnums;
import cashflow.service.AccountStatusService;
import cashflow.utils.CachedBodyHttpServletRequestWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Cail Gainey
 * @since 2025/2/19 16:41
 **/
@Slf4j
@Component
public class SqlInjectionFilter extends OncePerRequestFilter {
    private static final String TOKEN_HEADER = "token";
    private static final String SQL_INJECTION_PATTERN = ".*(\\bunion\\b|\\bselect\\b.*\\bfrom\\b|\\binsert\\b.*\\binto\\b|\\bdelete\\b.*\\bfrom\\b|\\bdrop\\b|--|#|\\bor\\b|\\band\\b|1=1|\\bexec\\b|\\bxp_cmdshell\\b|\\btruncate\\b|\\bdeclare\\b|\\bsleep\\b|\\bwaitfor\\b|\\bbenchmark\\b).*";
    private static final String APPLICATION_JSON = "application/json";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final int MAX_REQUEST_BODY_SIZE = 2 * 1024 * 1024; // 2MB

    private final Pattern sqlInjectionPattern = Pattern.compile(SQL_INJECTION_PATTERN, Pattern.CASE_INSENSITIVE);

    @Resource
    private AccountStatusService accountStatusService; // Inject AccountStatusService

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        // 排除文件上传请求
        String contentType = request.getContentType();
        if (contentType != null && contentType.startsWith(MULTIPART_FORM_DATA)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 白名单机制
        if (isWhitelisted(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 包装请求以缓存请求体（仅对非文件上传请求）
        CachedBodyHttpServletRequestWrapper wrappedRequest = new CachedBodyHttpServletRequestWrapper(request);

        // 检查 URL 路径
        if (isSqlInjection(wrappedRequest.getRequestURI())) {
            handleSqlInjection(wrappedRequest, response, "requestURI", wrappedRequest.getRequestURI());
            return; // 终止请求处理
        }

        // 检查 URL 参数
        Enumeration<String> parameterNames = wrappedRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = wrappedRequest.getParameter(paramName);

            if ((paramName != null && isSqlInjection(paramName)) || (paramValue != null && isSqlInjection(paramValue))) {
                handleSqlInjection(wrappedRequest, response, paramName, paramValue);
                return; // 终止请求处理
            }
        }

        // 检查 HTTP 头
        Enumeration<String> headerNames = wrappedRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = wrappedRequest.getHeader(headerName);

            if ((headerName != null && isSqlInjection(headerName)) || (headerValue != null && isSqlInjection(headerValue))) {
                handleSqlInjection(wrappedRequest, response, headerName, headerValue);
                return; // 终止请求处理
            }
        }

        // 检查请求体
        if (APPLICATION_JSON.equalsIgnoreCase(contentType)) {
            try {
                String requestBody = getRequestBody(wrappedRequest);
                ObjectMapper objectMapper = new ObjectMapper();
                // 使用 TypeReference 确保类型安全
                Map<String, Object> jsonMap = objectMapper.readValue(requestBody, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                });
                if (checkJsonForSqlInjection(jsonMap, wrappedRequest, response)) {
                    return; // 终止请求处理
                }
            } catch (JsonProcessingException e) {
                log.error("JSON 解析失败", e);
                sendErrorResponse(response, "请求体格式错误");
                return; // 终止请求处理
            } catch (IOException e) {
                log.error("解析请求体时发生错误", e);
                sendErrorResponse(response, "请求体解析错误");
                return; // 终止请求处理
            }
        } else if (APPLICATION_X_WWW_FORM_URLENCODED.equalsIgnoreCase(contentType)) {
            try {
                String requestBody = getRequestBody(wrappedRequest);
                String decodedBody = URLDecoder.decode(requestBody, StandardCharsets.UTF_8);
                if (isSqlInjection(decodedBody)) {
                    handleSqlInjection(wrappedRequest, response, "requestBody", decodedBody);
                    return; // 终止请求处理
                }
            } catch (IOException e) {
                log.error("解析请求体时发生错误", e);
                sendErrorResponse(response, "请求体解析错误");
                return; // 终止请求处理
            }
        }

        // 如果没有 SQL 注入攻击，继续执行过滤链
        filterChain.doFilter(wrappedRequest, response);
    }

    private void handleSqlInjection(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, String paramName, String paramValue) throws IOException {
        String clientIP = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        log.warn("Firewall: SQL拦截: IP={}, User-Agent={}, Param={}, Value={}", clientIP, userAgent, paramName, paramValue);


        String token = request.getHeader(TOKEN_HEADER);

        // 记录异常行为
        if (token != null) {
            accountStatusService.saveStatus(token, AccountStatusEnums.BAN.getStatus());
        }

        sendErrorResponse(response, "检测到SQL注入攻击，请停止非法操作！");
    }

    private boolean isSqlInjection(@NotNull String input) {
        // 排除正常的增删查改操作
        if (input.matches(".*(\\bselect\\b.*\\bfrom\\b|\\binsert\\b.*\\binto\\b|\\bupdate\\b.*\\bset\\b|\\bdelete\\b.*\\bfrom\\b).*")) {
            return false;
        }
        return sqlInjectionPattern.matcher(input).matches();
    }

    private boolean checkJsonForSqlInjection(@NotNull Map<String, Object> jsonMap, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();

            if (paramValue instanceof Map) {
                // 递归检查嵌套的 JSON 对象
                if (checkJsonForSqlInjection((Map<String, Object>) paramValue, request, response)) {
                    return true; // 检测到 SQL 注入
                }
            } else if (paramValue instanceof List) {
                // 检查 JSON 数组
                for (Object item : (List<?>) paramValue) {
                    if (item instanceof Map) {
                        if (checkJsonForSqlInjection((Map<String, Object>) item, request, response)) {
                            return true; // 检测到 SQL 注入
                        }
                    } else if (item != null && isSqlInjection(item.toString())) {
                        handleSqlInjection(request, response, paramName, item.toString());
                        return true; // 检测到 SQL 注入
                    }
                }
            } else if (paramValue != null && isSqlInjection(paramValue.toString())) {
                handleSqlInjection(request, response, paramName, paramValue.toString());
                return true; // 检测到 SQL 注入
            }
        }
        return false; // 未检测到 SQL 注入
    }

    private @NotNull String getRequestBody(@NotNull CachedBodyHttpServletRequestWrapper request) throws IOException {
        if (request.getContentLength() > MAX_REQUEST_BODY_SIZE) {
            throw new IOException("请求体过大");
        }

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    private boolean isWhitelisted(@NotNull HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/api/public");
    }

    private void sendErrorResponse(@NotNull HttpServletResponse response, @NotNull String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);
        response.flushBuffer(); // 确保响应被发送
    }
}
