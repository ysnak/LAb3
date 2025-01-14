package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SqlRequestController {

    private final DataSource dataSource;

    public SqlRequestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

//    @PostMapping("/execute-sql")
//    public String executeSqlQuery(@RequestParam("sqlQuery") String sqlQuery, Model model) {
//        try (var connection = dataSource.getConnection();
//             Statement stmt = connection.createStatement()) {
//
//            boolean hasResultSet = stmt.execute(sqlQuery);
//            if (hasResultSet) {
//                try (ResultSet rs = stmt.getResultSet()) {
//                    List<Map<String, Object>> results = new ArrayList<>();
//                    ResultSetMetaData metaData = rs.getMetaData();
//                    int columnCount = metaData.getColumnCount();
//
//                    while (rs.next()) {
//                        Map<String, Object> row = new HashMap<>();
//                        for (int i = 1; i <= columnCount; i++) {
//                            row.put(metaData.getColumnName(i), rs.getObject(i));
//                        }
//                        results.add(row);
//                    }
//
//                    model.addAttribute("results", results);
//                }
//            } else {
//                int updateCount = stmt.getUpdateCount();
//                model.addAttribute("message", "Query executed successfully. Rows affected: " + updateCount);
//            }
//            return "result";
//        } catch (SQLException e) {
//            model.addAttribute("message", "Error while executing SQL query: " + e.getMessage());
//            return "result";
//        }
//    }

    @PostMapping("/execute-sql")
    public String executeSqlQuery(@RequestParam("sqlQuery") String sqlQuery, Model model) {
        try (var connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            System.out.println("Executing SQL query: " + sqlQuery);

            boolean hasResultSet = stmt.execute(sqlQuery);

            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    List<Map<String, Object>> results = new ArrayList<>();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        results.add(row);
                    }

                    model.addAttribute("results", results);  // Передаем результаты на страницу
                }
            } else {
                int updateCount = stmt.getUpdateCount();
                model.addAttribute("message", "Query executed successfully. Rows affected: " + updateCount);
            }

            return "result";
        } catch (SQLException e) {
            model.addAttribute("message", "Error while executing SQL query: " + e.getMessage());
            return "result";
        }
    }


//    @PostMapping("/execute-sql")
//    public String executeSqlQuery(@RequestParam("sqlQuery") String sqlQuery,
//                                  @RequestParam(value = "params", required = false) List<String> params,
//                                  Model model) {
//        try (var connection = dataSource.getConnection()) {
//
//            // Проверка на то, если SQL-запрос содержит параметры типа ? (место для подставляемых значений)
//            try (PreparedStatement stmt = connection.prepareStatement(sqlQuery)) {
//
//                // Если параметры переданы, то они подставляются в запрос
//                if (params != null) {
//                    for (int i = 0; i < params.size(); i++) {
//                        // Подстановка строковых параметров
//                        stmt.setString(i + 1, params.get(i));
//                    }
//                }
//
//                boolean hasResultSet = stmt.execute();
//                if (hasResultSet) {
//                    try (ResultSet rs = stmt.getResultSet()) {
//                        List<Map<String, Object>> results = new ArrayList<>();
//                        ResultSetMetaData metaData = rs.getMetaData();
//                        int columnCount = metaData.getColumnCount();
//
//                        while (rs.next()) {
//                            Map<String, Object> row = new HashMap<>();
//                            for (int i = 1; i <= columnCount; i++) {
//                                row.put(metaData.getColumnName(i), rs.getObject(i));
//                            }
//                            results.add(row);
//                        }
//
//                        model.addAttribute("results", results);
//                    }
//                } else {
//                    int updateCount = stmt.getUpdateCount();
//                    model.addAttribute("message", "Query executed successfully. Rows affected: " + updateCount);
//                }
//                return "result";
//            }
//        } catch (SQLException e) {
//            model.addAttribute("message", "Error while executing SQL query: " + e.getMessage());
//            return "result";
//        }
//    }


}
