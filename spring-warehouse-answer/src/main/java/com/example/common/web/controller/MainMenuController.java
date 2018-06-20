package com.example.common.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainMenuController {
	@GetMapping("/")
	public String home() {
		return "main_menu";
	}
	
	// 演習用に作ったerror.htmlの表示用のメソッドです。
	@GetMapping("/sample/error")
	public String error() {
		throw new RuntimeException();
	}
}
