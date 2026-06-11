package org.example.lease.web.app.custom.interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.lease.common.login.LoginUser;
import org.example.lease.common.login.LoginUserHolder;
import org.example.lease.common.utils.JWTUtil;
import org.example.lease.model.entity.UserInfo;
import org.example.lease.model.enums.BaseStatus;
import org.example.lease.web.app.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 处理OPTIONS请求（CORS Preflight）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        try {
            // 从Authorization header中获取token，格式为 "Bearer {token}"
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || authHeader.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"msg\":\"缺少认证令牌\"}");
                return false;
            }

            // 处理Bearer前缀
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            if (token.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"msg\":\"无效的认证令牌\"}");
                return false;
            }

            // 解析JWT
            Claims claims = JWTUtil.parseJWT(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);

            if (userId == null || username == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"msg\":\"令牌信息不完整\"}");
                return false;
            }

            // 检查用户状态
            UserInfo userInfo = userInfoService.getById(userId);
            if (userInfo == null || userInfo.getStatus() == null || userInfo.getStatus() == BaseStatus.DISABLE) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"msg\":\"你的账号已被禁止登录，请联系管理员\"}");
                return false;
            }

            LoginUserHolder.setLoginUser(new LoginUser(userId, username));
            return true;
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"msg\":\"令牌无效或已过期\"}");
            return false;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"msg\":\"认证过程出错\"}");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        LoginUserHolder.clear();
    }
}
