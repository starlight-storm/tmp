package com.example.goods.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoodsMenuController {
	@GetMapping("/goods/menu")
	public String show() {
		return "/goods/goods_menu";
	}
}
