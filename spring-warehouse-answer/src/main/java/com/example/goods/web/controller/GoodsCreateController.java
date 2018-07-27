package com.example.goods.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public String prepare(Goods goods, Model model) {
		model.addAttribute("goods", goods);
		return "/goods/goods_create_input";
	}

	@PostMapping("/{code}/create/confirm")
	public String confirm(@Valid Goods goods, Errors errors) throws GoodsCodeDupulicateException, GoodsDeletedException {
		if (errors.hasErrors()) {
			return "/goods/goods_create_input";
		}
		
		goodsService.checkGoodsCanCreate(goods.getCode());
		return "redirect:/goods/{code}/create/confirmed";
	}

	@GetMapping("/{code}/create/confirmed")
	public String confirmed() {
		return "/goods/goods_create_confirm";
	}

	@PostMapping(value="/{code}/create/complete", params="create")
	public String complete(Goods goods, Errors errors) throws GoodsDeletedException, GoodsCodeDupulicateException {
		goodsService.createGoods(goods);
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
	
	@ExceptionHandler(GoodsDeletedException.class)
	public String handleGoodsDeleted(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.deleted");
	    return "redirect:/goods/create/input";
	}
	
	@ExceptionHandler(GoodsCodeDupulicateException.class)
	public String handleGoodsCodeDuplicate(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.duplicate");
	    return "redirect:/goods/create/input";
	}
}
