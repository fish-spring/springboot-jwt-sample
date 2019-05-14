package com.github.fish56.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 监测请求头Authorization中的token，来验证用户的合法性
 * 验证通过的话就将相关信息添加到SecurityContextHolder.getContext()
 */
@Slf4j
@Component
public class JwtTokenFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
        throws IOException, ServletException {

        log.info("正在处理 " + ((HttpServletRequest) req).getPathInfo());

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
        log.info("解析到token: " + token);

        if (token != null /*&& jwtTokenProvider.validateToken(token)*/) {
            log.info("解析到token: " + token);
            Authentication auth = jwtTokenProvider.getAuthentication(token);

            if (auth != null) {
                /**
                 * 这样代码好像是关键，目前还不是很理解
                 *   SecurityContext这个应该是程序启动时创建的全局对象
                 */
                log.info("认证成功");
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(req, res);
    }

}
