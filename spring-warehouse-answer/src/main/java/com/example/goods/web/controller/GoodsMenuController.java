package com.example.goods.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.support.SessionStatus;

@Controller
public class GoodsMenuController {
	@GetMapping("/goods/menu")
	public String show(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "/goods/goods_menu";
	}
}
