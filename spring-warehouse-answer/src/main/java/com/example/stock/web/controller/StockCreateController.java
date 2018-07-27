package com.example.stock.web.controller;

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

import com.example.goods.exception.GoodsCodeDupulicateException;
import com.example.goods.exception.GoodsDeletedException;
import com.example.goods.exception.NoGoodsException;
import com.example.stock.domain.Stock;
import com.example.stock.exception.StockDeletedException;
import com.example.stock.service.StockService;

@Controller
@RequestMapping("/stock")
@SessionAttributes("stock")
public class StockCreateController {
	@Autowired
	StockService stockService;

	@GetMapping("/create/input")
	public String prepare(Stock stock, Model model) {
		model.addAttribute("stock", stock);
		return "/stock/stock_create_input";
	}
	
	@PostMapping("/{code}/create/confirm")
	public String confirm(@Valid Stock stock, Errors errors) throws GoodsCodeDupulicateException, StockDeletedException {
		if (errors.hasErrors()) {
			return "/stock/stock_create_input";
		}
		
		if(!stockService.isStockCreate(stock.getGoodsCode())) {
			throw new StockDeletedException();
		}
		return "redirect:/stock/{code}/create/confirmed";
	}

	@GetMapping("/{code}/create/confirmed")
	public String confirmed() {
		return "/stock/stock_create_confirm";
	}
	
	@PostMapping(value="/{code}/create/complete", params="create")
	public String complete(Stock stock, Errors errors) throws GoodsDeletedException, GoodsCodeDupulicateException, NoGoodsException, StockDeletedException {
		stockService.createStock(stock);
		return "redirect:/stock/{code}/create/completed";
	}

	@PostMapping(value="/{code}/create/complete", params="back")
	public String back() {
		return "/stock/stock_create_input";
	}

	@GetMapping("/{code}/create/completed")
	public String completed(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "/stock/stock_create_complete";
	}
	
	@ExceptionHandler(StockDeletedException.class)
	public String handleStockDeleted(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.stock.data.deleted");
	    return "redirect:/stock/create/input";
	}
	
	@ExceptionHandler(GoodsCodeDupulicateException.class)
	public String handleGoodsCodeDuplicate(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.duplicate");
	    return "redirect:/stock/create/input";
	}
	
	@ExceptionHandler(GoodsDeletedException.class)
	public String handleGoodsDeleted(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.deleted");
	    return "redirect:/stock/create/input";
	}
	
	@ExceptionHandler(NoGoodsException.class)
	public String handleNoGoods(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.notfound");
	    return "redirect:/stock/create/input";
	}
}
