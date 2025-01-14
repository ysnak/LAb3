package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

@Controller
public class AccountUserController {

    private final DataSource dataSource;

    @Autowired
    public AccountUserController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/login-page")
    public String showLoginPage() {

        return "login-page";  // возвращаем страницу входа
    }

    @GetMapping("/authorization-page")
    public String showAuthorizationPage() {
        return "authorization-page";
    }

    @PostMapping("/login-page")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model) {
        System.out.println("Entered login method");

        StringBuilder resultMessage = new StringBuilder();
        String sql = "SELECT password, role FROM users WHERE username = ?";

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            resultMessage.append("Username or password cannot be empty");
            model.addAttribute("resultMessage", resultMessage.toString());
            return "login-page";
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedEncodedPassword = rs.getString("password");
                    String role = rs.getString("role");
                    String decodedPassword = new String(Base64.getDecoder().decode(storedEncodedPassword), StandardCharsets.UTF_8);

                    if (decodedPassword.equals(password)) {
                        System.out.println("Password matches! Role: " + role);
                    } else {
                        resultMessage.append("Invalid username or password");
                    }
                } else {
                    resultMessage.append("Invalid username or password");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultMessage.append("Error: ").append(e.getMessage());
        }

        model.addAttribute("resultMessage", resultMessage.toString());
        return "login-page";
    }
}


