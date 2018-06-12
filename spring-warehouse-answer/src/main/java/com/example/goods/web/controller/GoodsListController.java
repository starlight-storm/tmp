package com.example.goods.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.goods.domain.Goods;
import com.example.goods.exception.NoGoodsException;
import com.example.goods.service.GoodsService;

@Controller
@RequestMapping("/goods")
public class GoodsListController {
	@Autowired
	private GoodsService goodsService;
	
	@ModelAttribute
	public List<Goods> setGoodsList() {
	    return new ArrayList<Goods>();
	}

	@GetMapping("/find/list")
	public String complete(List<Goods> goodsList, Errors errors, Model model) {
		try {
			goodsList = goodsService.findAllGoods();
			model.addAttribute("goodsList", goodsList);
		} catch (NoGoodsException e) {
			errors.reject("errors.goods.data.notfound"); // TODO: 試してない
		}
		return "/goods/goods_list";
	}
}