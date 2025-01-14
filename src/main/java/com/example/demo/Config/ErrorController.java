package com.example.demo.Config;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ErrorController {

    @GetMapping("/error")
    public String errorPage() {
        return "error"; // Название HTML-файла
    }

    @GetMapping("/error-403")
    public String errorPage403() {
        return "error-403"; // Название HTML-файла
    }

    @GetMapping("/error-404")
    public String errorPage404() {
        return "error-404"; // Название HTML-файла
    }

}