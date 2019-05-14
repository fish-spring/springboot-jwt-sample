package com.github.fish56.security.jwt;

import com.github.fish56.domain.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 创建、验证token
 */
@Slf4j
@Component
public class JwtTokenProvider {

    /**
     * 给出默认的密钥，可以在配置文件中覆盖
     */
    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

    /**
     * 设置默认token有效时间为1h，可以在配置文件中覆盖
     */
    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000;

    // @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 将密钥用base64编码
     */
    @PostConstruct
    protected void init() {
        log.info("secretKey是" + secretKey);
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        log.info("secretKey Base64编码后是" + secretKey);
    }

    /**
     * 创建一个token
     * @param username 用户名
     * @param roles 用户角色
     * @return 当前secret签发的jwt
     */
    public String createToken(String username, List<String> roles) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        //UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        //return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        log.info("正在验证" + token);
        User userDetails = new User();
        userDetails.setUsername("Jon");
        List<String> roles = new LinkedList<>();
        roles.add("USER");
        roles.add("VIP");
        userDetails.setRoles(roles);
        return new UsernamePasswordAuthenticationToken(userDetails,"", userDetails.getAuthorities());
    }

    /**
     * 解析出用户名
     * @param token jwt
     * @return 用户名
     */
    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 解析出token的body
     * @param token
     * @return
     */
    public Claims getClaims(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从请求报文中解析出token
     * @param req
     * @return
     */
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 验证token是否优先(包含了过期校验)
     * @param token jwt
     * @return 是否合法
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }
    }

}
