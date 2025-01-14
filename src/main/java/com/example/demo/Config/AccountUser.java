package com.example.demo.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

@Controller
public class AccountUser {

    private final DataSource dataSource;

    @Autowired
    public AccountUser(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        StringBuilder usersResults = new StringBuilder();
        String sql = "SELECT * FROM users;";

        try (var connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                usersResults.append("There are no records in the user table.");
            } else {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String role = rs.getString("role");


                    usersResults.append(String.format("ID: %d, Username: %s, Password: %s, Name: %s  <br/>",
                            id, username, password, role));
                }
            }
        } catch (SQLException e) {
            usersResults.append("Error while getting list of sportsmans: ").append(e.getMessage());
        }

        model.addAttribute("usersResults", usersResults.toString());
        return "users";

    }

    @GetMapping("/create-account-user")
    public String showCreateUserForm() {
        return "create-account-user";
    }


    @PostMapping("/create-account-user")
    public String createUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("role") String role,
                             Model model) {
        StringBuilder resultMessage = new StringBuilder();

        if (!role.equals("USER") && !role.equals("ADMIN")) {
            resultMessage.append("Invalid role. The role must be either 'USER' or 'ADMIN'.");
            model.addAttribute("resultMessage", resultMessage.toString());
            return "create-account-user";
        }

        String checkUserSql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(checkUserSql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                resultMessage.append("User with this username already exists.");
                model.addAttribute("resultMessage", resultMessage.toString());
                return "create-account-user";
            }
        } catch (SQLException e) {
            resultMessage.append("Error: ").append(e.getMessage());
            model.addAttribute("resultMessage", resultMessage.toString());
            return "create-account-user";
        }

        String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
        System.out.println("Encoded password (Base64): " + encodedPassword);

        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, encodedPassword);
            pstmt.setString(3, role);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                resultMessage.append("User created successfully!");
                return "redirect:/login-page";
            } else {
                resultMessage.append("Error occurred while creating user.");
            }
        } catch (SQLException e) {
            resultMessage.append("Error: ").append(e.getMessage());
        }

        model.addAttribute("resultMessage", resultMessage.toString());
        return "create-account-user";
    }

}

