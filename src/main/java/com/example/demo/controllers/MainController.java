package com.example.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        model.addAttribute("title", "Databases");
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(", "));

        model.addAttribute("username", username);
        model.addAttribute("role", role);
        return "home";
    }

}
