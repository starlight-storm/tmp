package com.example.goods.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.goods.domain.Goods;
import com.example.goods.exception.NoGoodsException;
import com.example.goods.service.GoodsService;
import com.example.goods.web.modelattribute.GoodsCode;

@Controller
@RequestMapping("/goods")
public class GoodsFindController {
	@Autowired
	private GoodsService goodsService;

	@GetMapping("/find/input")
	public String prepare(GoodsCode goodsCode) {
		return "/goods/goods_find_input";
	}

	@GetMapping("/{code}")
	public String complete(@Valid GoodsCode goodsCode, Errors errors, Model model) throws NoGoodsException {
		if (errors.hasErrors()) {
			return "/goods/goods_find_input";
		}

		Goods goods = goodsService.findGoods(goodsCode.getCode());
		model.addAttribute("goods", goods);
		return "/goods/goods_find_complete";
	}
	
	@ExceptionHandler(NoGoodsException.class)
	public String handleNoGoods(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.notfound");
	    return "redirect:/goods/find/input";
	}
}
