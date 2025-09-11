/* This project is licensed under the Mulan PSL v2.
 You can use this software according to the terms and conditions of the Mulan PSL v2.
 You may obtain a copy of Mulan PSL v2 at:
     http://license.coscl.org.cn/MulanPSL2
 THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 PURPOSE.
 See the Mulan PSL v2 for more details.
 Create: 2025
*/

package com.authcenter.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kong.unirest.Cookie;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;

/**
 * 拦截器类，用于进行身份验证拦截.
 */
public class AuthingInterceptor implements HandlerInterceptor {
    /**
     * 日志记录器，用于记录身份验证拦截器的日志信息.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthingInterceptor.class);

    @Value("${account.person.center.url}")
    private String accountUrl;

    @Value("${auth.api.token:}")
    private String apiToken;

    /**
     * 预处理方法，在请求处理之前调用，用于进行预处理操作.
     *
     * @param httpServletRequest HTTP 请求对象
     * @param httpServletResponse HTTP 响应对象
     * @param object 处理器
     * @return 返回布尔值表示是否继续处理请求
     * @throws Exception 可能抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse, Object object) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }

        // 检查有没有需要用户权限的注解，仅拦截AuthingToken和AuthingUserToken
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        if (method.isAnnotationPresent(AuthingApiToken.class)) {
            if (!checkApiToken(httpServletRequest)) {
                LOGGER.error("api unauthorized");
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "unauthorized");
                return false;
            }
        }

        if (!method.isAnnotationPresent(AuthingUserToken.class)) {
            return true;
        }
        AuthingUserToken authingUserToken = method.getAnnotation(AuthingUserToken.class);
        if (authingUserToken == null || !authingUserToken.required()) {
            return true;
        }
        String token = httpServletRequest.getHeader("token");
        String referer = httpServletRequest.getHeader("referer");
        String cookie = httpServletRequest.getHeader("cookie");
        HttpResponse<JsonNode> response = Unirest.get(accountUrl)
                .header("token", token)
                .header("referer", referer)
                .header("cookie", cookie)
                .asJson();
        if (response.getStatus() != 200) {
            LOGGER.error("unauthorized {}", response.getBody().toString());
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "unauthorized");
            return false;
        }
        httpServletRequest.setAttribute("username", response.getBody().getObject().getJSONObject("data")
                .getString("username"));
        if (response.getCookies() != null) {
            for (Cookie cookieStr : response.getCookies()) {
                // 解析cookie字符串并添加到响应头
                httpServletResponse.addHeader("Set-Cookie", cookieStr.toString());
            }
        }
        return true;
    }

    private Boolean checkApiToken(HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("token");
        if (StringUtils.isBlank(apiToken) || !apiToken.equals(token)) {
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }
}
