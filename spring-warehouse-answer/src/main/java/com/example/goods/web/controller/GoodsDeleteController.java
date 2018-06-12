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
import com.example.goods.exception.GoodsDeletedException;
import com.example.goods.exception.NoGoodsException;
import com.example.goods.service.GoodsService;
import com.example.goods.web.modelattribute.GoodsCode;

@Controller
@RequestMapping("/goods")
@SessionAttributes("goods")
public class GoodsDeleteController {
	
	@Autowired
	private GoodsService goodsService;
	
	@GetMapping("/delete/input")
	public String input(GoodsCode goodsCode) {
		System.out.println("***** input *****");
		return "/goods/goods_delete_input";
	}
	
	@PostMapping("/{code}/delete/confirm")
	public String confirm(@Valid GoodsCode goodsCode, Errors errors, Model model) {
		System.out.println("***** confirm *****");
		if (errors.hasErrors()) {
			System.out.println("***** confirm : hasErrors *****");
            return "/goods/goods_delete_input";
        }

		try {
			if(goodsService.isGoodsDeactive(goodsCode.getCode())) {
				errors.reject("errors.goods.data.deleted");
				return "/goods/goods_delete_input";
			}
		} catch (NoGoodsException e) {
			errors.reject("errors.goods.data.notfound");
			return "/goods/goods_delete_input";
		}

		Goods goods = goodsService.findGoods(goodsCode.getCode());
		model.addAttribute("goods", goods);
		return "redirect:/goods/{code}/delete/confirmed";
	}
	
    @GetMapping("/{code}/delete/confirmed")
    public String confirmed() {
    		System.out.println("***** deleteConfirm *****");
        return "/goods/goods_delete_confirm";
    }
    
    @PostMapping("/{code}/delete/complete")
    public String complete(Goods goods, Errors errors) {
		int goodsCode = goods.getCode();
		
		System.out.println("***** complete: " + goodsCode + "*****");
		
		try {
			goodsService.deleteGoods(goodsCode);
		} catch (NoGoodsException e) {
			errors.reject("errors.goods.data.notfound");
			return "/goods/goods_delete_input";
		} catch (GoodsDeletedException e) {
			errors.reject("errors.goods.data.deleted");
			return "/goods/goods_delete_input";
		}
		return "redirect:/goods/{code}/delete/completed";
    }
    
    @GetMapping("/{code}/delete/completed")
    public String completed(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "/goods/goods_delete_complete";
    }
}
