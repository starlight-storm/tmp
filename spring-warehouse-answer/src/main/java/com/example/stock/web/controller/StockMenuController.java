package com.example.stock.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StockMenuController {
	@GetMapping("/stock/menu")
	public String show() {
		return "/stock/stock_menu";
	}
}
