package com.example.common.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainMenuController {
	@GetMapping("/")
	public String home() {
		return "main_menu";
	}
}
