package com.github.fish56.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class RootController {
    @GetMapping("/")
    public String index(@AuthenticationPrincipal Principal principal){
        String name = principal != null ? principal.getName() : "";
        return String.format("你好 %s", name);
    }
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal Principal principal){
        String name = principal != null ? principal.getName() : "";
        return String.format("你好vip %s", name);
    }
    @GetMapping("/vip")
    public String vip(@AuthenticationPrincipal Principal principal){
        String name = principal != null ? principal.getName() : "";
        return String.format("你好vip %s", name);
    }
    @GetMapping("/admin")
    public String admin(@AuthenticationPrincipal Principal principal){
        String name = principal != null ? principal.getName() : "";
        return String.format("你好admin %s", name);
    }
}
