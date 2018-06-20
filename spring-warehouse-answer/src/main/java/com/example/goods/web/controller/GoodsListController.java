package com.example.goods.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.goods.domain.Goods;
import com.example.goods.exception.NoGoodsException;
import com.example.goods.service.GoodsService;

@Controller
@RequestMapping("/goods")
public class GoodsListController {
	@Autowired
	private GoodsService goodsService;

	@ModelAttribute
	public PagedListHolder<Goods> setGoodsList() {
		return new PagedListHolder<>();
	}

	private static int PAGE_SIZE = 3;
	
	@GetMapping("/find/list")
	public String show(@RequestParam(value = "page", defaultValue = "0") String page, PagedListHolder<Goods> goodsList, Errors errors, Model model) {
		try {
			//goodsList = goodsService.findAllGoods();
			
			goodsList = new PagedListHolder<>(goodsService.findAllGoods());
			goodsList.setPage(Integer.parseInt(page));
			goodsList.setPageSize(PAGE_SIZE);
			model.addAttribute("goodsList", goodsList);
		} catch (NoGoodsException e) {
			errors.reject("errors.goods.data.notfound");
		}
		return "/goods/goods_list";
	}
}