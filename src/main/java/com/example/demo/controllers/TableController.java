package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.DataSource;

@Controller
public class TableController {

 private final DataSource dataSource;

@Autowired
public TableController(DataSource dataSource) {
    this.dataSource = dataSource;
}

@GetMapping("/create-entry")
public String createEntry(Model model) {
    model.addAttribute("title", "Create New Entry");
    return "create-entry";
}

@GetMapping("/select-table")
public String selectTablePage() {
    return "select-table";
}

@GetMapping("/delete-by-key")
public String showDeleteByKeyForm() {
    return "delete-by-key";
}


@GetMapping("/edit-table")
public String getEditTableForm() {
    return "edit-table";
}


@GetMapping("/request-table")
public String showRequestTablePage(Model model) {
    return "request-table";
}


}

