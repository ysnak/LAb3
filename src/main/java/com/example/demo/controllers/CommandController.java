package com.example.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class CommandController {


    private final DataSource dataSource;

    @Autowired

    public CommandController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/command")
    public String listCommand(Model model) {
        StringBuilder commandResults = new StringBuilder();
        String sql = "SELECT * FROM command;";

        try (var connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                commandResults.append("There are no records in the command table.");
            } else {
                while (rs.next()) {
                    int command_id = rs.getInt("command_id");
                    String command_name = rs.getString("command_name");

                    commandResults.append(String.format("ID: %d, Name: %s<br/>",
                            command_id, command_name));
                }
            }
        } catch (SQLException e) {
            commandResults.append("Error while getting list of command: ").append(e.getMessage());
        }

        model.addAttribute("commandResults", commandResults.toString());
        return "command";
    }

    @GetMapping("/command-create")
    public String showCreateCommandForm() {
        return "command-create";
    }

    @PostMapping("/command-create")
    public String createCommand(@RequestParam("command_name") String name,
                              Model model) {
        String sql = "INSERT INTO command (command_name) VALUES (?)";

        try (var connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            pstmt.executeUpdate();
            model.addAttribute("message", "New entry created successfully!");
        } catch (SQLException e) {
            model.addAttribute("message", "Error while creating new entry: " + e.getMessage());
        }

        return "redirect:/command";
    }

    @GetMapping("/command-delete")
    public String getCommandDeleteForm() {
        return "command-delete";
    }


    @PostMapping("/command-delete")
    public String deleteCommand(@RequestParam("command_id") int id, Model model) {
        String sql = "DELETE FROM command WHERE command_id = ?";

        try (var connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                model.addAttribute("message", "Entry deleted successfully!");
            } else {
                model.addAttribute("message", "Entry not found or already deleted.");
            }
        } catch (SQLException e) {
            model.addAttribute("message", "Error while deleting entry: " + e.getMessage());
        }

        return "redirect:/command";
    }

    @GetMapping("/command-edit")
    public String getUpdateCommandForm() {
        return "command-edit";
    }

    @PostMapping("/command-edit")
    public String updateCommand(@RequestParam("command_id") int id,
                              @RequestParam("command_name") String name,
                              Model model) {
        String sql = "UPDATE command SET command_name = ? WHERE command_id = ?";

        try (var connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, id);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                model.addAttribute("message", "Record updated successfully!");
            } else {
                model.addAttribute("message", "Record not found.");
            }
        } catch (SQLException e) {
            model.addAttribute("message", "Error while updating record: " + e.getMessage());
        }

        return "redirect:/command";
    }
}
