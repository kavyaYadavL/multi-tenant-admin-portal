package com.internship.tool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.Filter;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    public Filter securityHeadersFilter() {
        return new Filter() {
            @Override
            public void doFilter(javax.servlet.ServletRequest request,
                                 javax.servlet.ServletResponse response,
                                 FilterChain chain)
                    throws IOException, ServletException {

                HttpServletResponse res = (HttpServletResponse) response;

                // 🔐 Security Headers
                res.setHeader("X-Content-Type-Options", "nosniff");
                res.setHeader("X-Frame-Options", "DENY");
                res.setHeader("X-XSS-Protection", "1; mode=block");
                res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                res.setHeader("Content-Security-Policy", "default-src 'self'");

                chain.doFilter(request, response);
            }
        };
    }
}
