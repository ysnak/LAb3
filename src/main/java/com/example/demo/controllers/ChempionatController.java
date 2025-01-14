package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class ChempionatController {

    private final DataSource dataSource;

    @Autowired
    public ChempionatController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/chempionat")
    public String listChempionat(Model model) {
        StringBuilder chempionatResults = new StringBuilder();
        String sql = "SELECT * FROM chempionat;";

        try (var connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                chempionatResults.append("There are no records in the chempionat table.");
            } else {
                while (rs.next()) {
                    int chempionat_id = rs.getInt("chempionat_id");
                    String chempionat_name = rs.getString("chempionat_name");
                    int chempionat_year = rs.getInt("chempionat_year");

                    // Исправлено: правильный порядок и соответствующий тип данных для форматирования
                    chempionatResults.append(String.format("ID: %d, Name: %s, Year: %d<br/>",
                            chempionat_id, chempionat_name, chempionat_year));
                }
            }
        } catch (SQLException e) {
            chempionatResults.append("Error while getting list of chempionat: ").append(e.getMessage());
        }

        model.addAttribute("chempionatResults", chempionatResults.toString());
        return "chempionat"; // имя шаблона для страницы с результатами
    }


    @GetMapping("/chempionat-create")
    public String showCreateChempionatForm() {
        return "chempionat-create";
    }

    @PostMapping("/chempionat-create")
    public String createChempionat(@RequestParam("chempionat_name") String name,
            @RequestParam("chempionat_year") int year,
                                  Model model) {
        String sql = "INSERT INTO chempionat (chempionat_name, " +
                "chempionat_year) VALUES (?, ?)";

        try (var connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, year);

            pstmt.executeUpdate();
            model.addAttribute("message", "New entry created successfully!");

        } catch (SQLException e) {
            model.addAttribute("message", "Error while creating new entry: " + e.getMessage());
        }

        return "redirect:/chempionat";
    }


    @GetMapping("/chempionat-delete")
    public String getChempionatDeleteForm() {
        return "chempionat-delete";
    }


    @PostMapping("/chempionat-delete")
    public String deleteChempionat(@RequestParam("chempionat_id") int id, Model model) {
        String sql = "DELETE FROM chempionat WHERE chempionat_id = ?";

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

        return "redirect:/chempionat";
    }
    @GetMapping("/chempionat-edit")
    public String getUpdateChempionatForm() {
        return "chempionat-edit";
    }

    @PostMapping("/chempionat-edit")
    public String updateChempionat(@RequestParam("chempionat_id") int id,
                                   @RequestParam("chempionat_name") String name,
                                  @RequestParam("chempionat_year") int year,

                                  Model model) {
        String sql = "UPDATE chempionat SET chempionat_name = ?" +
                ", chempionat_year = ? WHEREchempionat_id = ?";

        try (var connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, year);
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

        return "redirect:/chempionat";
    }

}
