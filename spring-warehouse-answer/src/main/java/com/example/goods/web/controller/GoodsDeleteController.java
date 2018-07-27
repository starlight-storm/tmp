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
import com.example.goods.exception.GoodsDeletedException;
import com.example.goods.exception.NoGoodsException;
import com.example.goods.service.GoodsService;
import com.example.goods.web.modelattribute.GoodsCode;

@Controller
@RequestMapping("/goods")
@SessionAttributes({"goods", "goodsCode"})
public class GoodsDeleteController {

	@Autowired
	private GoodsService goodsService;

	@GetMapping("/delete/input")
	public String prepare(GoodsCode goodsCode) {
		return "/goods/goods_delete_input";
	}

	@PostMapping("/{code}/delete/confirm")
	public String confirm(@Valid GoodsCode goodsCode, Errors errors, Model model) throws NoGoodsException, GoodsDeletedException {
		if (errors.hasErrors()) {
			return "/goods/goods_delete_input";
		}

		if (goodsService.isGoodsDeactive(goodsCode.getCode())) {
			throw new GoodsDeletedException();
		}

		Goods goods = goodsService.findGoods(goodsCode.getCode());
		model.addAttribute("goods", goods);
		return "redirect:/goods/{code}/delete/confirmed";
	}

	@GetMapping("/{code}/delete/confirmed")
	public String confirmed() {
		return "/goods/goods_delete_confirm";
	}

	@PostMapping(value="/{code}/delete/complete", params="delete")
	public String complete(Goods goods, Errors errors) throws GoodsDeletedException, NoGoodsException {
		int goodsCode = goods.getCode();

		goodsService.deleteGoods(goodsCode);
		return "redirect:/goods/{code}/delete/completed";
	}

	@PostMapping(value="/{code}/delete/complete", params="back")
	public String back() {
		return "/goods/goods_delete_input";
	}

	@GetMapping("/{code}/delete/completed")
	public String completed(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "/goods/goods_delete_complete";
	}
	
	@ExceptionHandler(GoodsDeletedException.class)
	public String handleGoodsDeleted(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.deleted");
	    return "redirect:/goods/delete/input";
	}
	
	@ExceptionHandler(NoGoodsException.class)
	public String handleNoGoods(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.notfound");
	    return "redirect:/goods/delete/input";
	}
}
