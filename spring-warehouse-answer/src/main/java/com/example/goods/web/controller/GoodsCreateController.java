package com.example.goods.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.example.goods.domain.Goods;
import com.example.goods.exception.GoodsCodeDupulicateException;
import com.example.goods.exception.GoodsDeletedException;
import com.example.goods.service.GoodsService;

@Controller
@RequestMapping("/goods")
@SessionAttributes("goods")
public class GoodsCreateController {

	@Autowired
	private GoodsService goodsService;

	@GetMapping("/create/input")
	public String input(Goods goods, Model model) {
		model.addAttribute("goods", goods);
		return "/goods/goods_create_input";
	}

	@PostMapping("/{code}/create/confirm")
	public String confirm(@Valid Goods goods, Errors errors) {
		if (errors.hasErrors()) {
			return "/goods/goods_create_input";
		}
		try {
			goodsService.canCreateGoods(goods.getCode());
			return "redirect:/goods/{code}/create/confirmed";
		} catch (GoodsDeletedException deletedException) {
			errors.reject("errors.goods.data.deleted");
			return "/goods/goods_create_input";
		} catch (GoodsCodeDupulicateException dupulicateException) {
			errors.reject("errors.goods.data.duplicate");
			return "/goods/goods_create_input";
		}
	}

	@GetMapping("/{code}/create/confirmed")
	public String confirmed() {
		return "/goods/goods_create_confirm";
	}

	@PostMapping(value="/{code}/create/complete", params="create")
	public String complete(Goods goods, Errors errors) {
		try {
			goodsService.createGoods(goods);
		} catch (GoodsDeletedException e) {
			errors.reject("errors.goods.data.deleted");
			return "/goods/goods_create_input";
		} catch (GoodsCodeDupulicateException e) {
			errors.reject("errors.goods.data.duplicate");
			return "/goods/goods_create_input";
		}
		return "redirect:/goods/{code}/create/completed";
	}

	@PostMapping(value="/{code}/create/complete", params="back")
	public String back() {
		return "/goods/goods_create_input";
	}

	@GetMapping("/{code}/create/completed")
	public String completed(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "/goods/goods_create_complete";
	}
}
