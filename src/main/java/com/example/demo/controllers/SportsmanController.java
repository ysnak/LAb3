package com.example.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SportsmanController {

    private final DataSource dataSource;

    @Autowired
    public SportsmanController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/sportsman")
    public String listSportsman(Model model) {
        StringBuilder sportsmanResults = new StringBuilder();
        String sql = "SELECT * FROM sportsman;";

        try (var connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                sportsmanResults.append("There are no records in the sportsman table.");
            } else {
                while (rs.next()) {
                    int sportsman_id = rs.getInt("sportsman_id");
                    String sportsman_name = rs.getString("sportsman_name");
                    int sportsman_age = rs.getInt("sportsman_age");

                    sportsmanResults.append(String.format("ID: %d, Name: %s, Age: %d<br/>",
                            sportsman_id, sportsman_name, sportsman_age));
                }
            }
        } catch (SQLException e) {
            sportsmanResults.append("Error while getting list of sportsmans: ").append(e.getMessage());
        }

        model.addAttribute("sportsmanResults", sportsmanResults.toString());
        return "sportsman";


    }

    @GetMapping("/sportsman-create")
    public String showCreateSportsmanForm() {
        return "sportsman-create";
    }

    @PostMapping("/sportsman-create")
    public String createSportsman(@RequestParam("sportsman_name") String name,
                                  @RequestParam("sportsman_age") int age,
                                  Model model) {
        String sql = "INSERT INTO sportsman ( sportsman_name, sportsman_age) VALUES (?, ?)";

        try (var connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);

            pstmt.executeUpdate();
            model.addAttribute("message", "New entry created successfully!");
        } catch (SQLException e) {
            model.addAttribute("message", "Error while creating new entry: " + e.getMessage());
        }

        return "redirect:/sportsman";
    }
    @GetMapping("/sportsman-delete")
    public String getSportsmantDeleteForm() {
        return "sportsman-delete";
    }


    @PostMapping("/sportsman-delete")
    public String deleteSportsman(@RequestParam("sportsman_id") int id, Model model) {
        String sql = "DELETE FROM sportsman WHERE sportsman_id = ?";

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

        return "redirect:/sportsman";
    }

    @GetMapping("/sportsman-edit")
    public String getUpdateSportsmanForm() {
        return "sportsman-edit";
    }

    @PostMapping("/sportsman-edit")
    public String updateSportsman(@RequestParam("sportsman_id") int id,
                                  @RequestParam("sportsman_name") String name,
                                  @RequestParam("sportsman_age") int age,
                                  Model model) {
        String sql = "UPDATE sportsman SET sportsman_name = ?, sportsman_age = ? WHERE sportsman_id = ?";
        try (var connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setInt(3, id);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                model.addAttribute("message", "Record updated successfully!");
            } else {
                model.addAttribute("message", "Record not found.");
            }
        } catch (SQLException e) {
            model.addAttribute("message", "Error while updating record: " + e.getMessage());
        }

        return "redirect:/sportsman";
    }


}
