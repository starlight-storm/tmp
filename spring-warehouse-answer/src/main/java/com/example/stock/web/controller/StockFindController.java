package com.example.stock.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.goods.web.modelattribute.GoodsCode;
import com.example.stock.domain.Stock;
import com.example.stock.exception.NoStockException;
import com.example.stock.service.StockService;

@Controller
@RequestMapping("/stock")
public class StockFindController {
	@Autowired
	private StockService stockService;

	@GetMapping("/find/input")
	public String prepare(GoodsCode goodsCode) {
		return "/stock/stock_find_input";
	}
	
	@GetMapping("/{code}")
	public String complete(@Valid GoodsCode goodsCode, Errors errors, Model model) throws NoStockException {
		if (errors.hasErrors()) {
			return "/stock/stock_find_input";
		}

		Stock stock = stockService.findStock(goodsCode.getCode());
		model.addAttribute("stock", stock);
		return "/stock/stock_find_complete";
	}
	
	@ExceptionHandler(NoStockException.class)
	public String handleNoStock(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.stock.data.notfound");
	    return "redirect:/stock/find/input";
	}
}
