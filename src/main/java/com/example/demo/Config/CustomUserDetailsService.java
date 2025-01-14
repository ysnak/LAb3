package com.example.demo.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private DataSource dataSource;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT password, role FROM users WHERE username = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPasswordBase64 = rs.getString("password");
                    String role = rs.getString("role");

                    String decodedPassword = new String(Base64.getDecoder().decode(storedPasswordBase64));

                    return User.builder()
                            .username(username)
                            .password(decodedPassword)
                            .roles(role)
                            .build();
                } else {
                    throw new UsernameNotFoundException("User not found: " + username);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}