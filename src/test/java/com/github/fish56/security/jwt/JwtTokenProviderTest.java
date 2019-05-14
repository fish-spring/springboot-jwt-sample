package com.github.fish56.security.jwt;

import com.alibaba.fastjson.JSONObject;
import com.github.fish56.DemoApplicationTest;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.junit.Assert.*;

@Slf4j
public class JwtTokenProviderTest extends DemoApplicationTest {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Test
    public void createToken() {
        val roles = new ArrayList<String>();
        roles.add("USER");
        roles.add("ADMIN");
        String token = tokenProvider.createToken("Jon", roles);

        log.info(String.format("成功创建token %s", token));
    }

    @Test
    public void parseToken() {
        val roles = new ArrayList<String>();
        roles.add("USER");
        roles.add("ADMIN");
        String token = tokenProvider.createToken("Jon", roles);

        assertTrue(tokenProvider.validateToken(token));

        String username = tokenProvider.getUsername(token);
        assertEquals("Jon", username);

        Claims claims = tokenProvider.getClaims(token);
        log.info(JSONObject.toJSONString(claims));
    }
}