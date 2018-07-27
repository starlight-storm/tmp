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
import com.example.stock.domain.InOutBoundLine;
import com.example.stock.domain.Stock;
import com.example.stock.exception.NoStockException;
import com.example.stock.exception.StockDeletedException;
import com.example.stock.exception.StockOverException;
import com.example.stock.service.StockService;
import com.example.stock.web.modelattribute.InBound;


@Controller
@RequestMapping("/stock")
@SessionAttributes("inBound")
public class StockInBoundController {
	
	@Autowired
	StockService stockService;
	
	@GetMapping("/inbound/input")
	public String prepare(InBound inBound, Model model) {
		model.addAttribute("inBound", inBound);
		return "/stock/stock_inbound_input";
	}

	@PostMapping("/{code}/inbound/confirm")
	public String confirm(@Valid InBound inBound, Errors errors, Model model) 
			throws GoodsCodeDupulicateException, StockDeletedException, NoStockException, StockOverException {
		if (errors.hasErrors()) {
			return "/stock/stock_inbound_input";
		}
		
		int goodsCode = inBound.getGoodsCode();
		int inBoundQuantity = inBound.getInBoundQuantity();
		
		Stock stock = stockService.findStock(goodsCode);
		stock.incrementQuantity(inBoundQuantity);
		
		return "redirect:/stock/{code}/inbound/confirmed";
	}

	@GetMapping("/{code}/inbound/confirmed")
	public String confirmed() {
		return "/stock/stock_inbound_confirm";
	}
	
	@PostMapping(value="/{code}/inbound/complete", params="inbound")
	public String complete(InBound inBound) 
			throws NoStockException, StockOverException {
		
		int goodsCode = inBound.getGoodsCode();
		int inBoundQuantity = inBound.getInBoundQuantity();
		
		InOutBoundLine inOutBoundLine = new InOutBoundLine(InOutBoundLine.OUTBOUND, goodsCode, inBoundQuantity);
		Stock stock = stockService.inBound(inOutBoundLine);
		
		Integer afterInBoundQuantity = stock.getQuantity();
		inBound.setAfterInBoundQuantity(afterInBoundQuantity);
		
		return "redirect:/stock/{code}/inbound/completed";
	}

	@PostMapping(value="/{code}/inbound/complete", params="back")
	public String back() {
		return "/stock/stock_inbound_input";
	}

	@GetMapping("/{code}/inbound/completed")
	public String completed(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "/stock/stock_inbound_complete";
	}
	
	@ExceptionHandler(StockDeletedException.class)
	public String handleStockDeleted(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.stock.data.deleted");
	    return "redirect:/stock/inbound/input";
	}
	
	@ExceptionHandler(GoodsCodeDupulicateException.class)
	public String handleGoodsCodeDuplicate(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.duplicate");
	    return "redirect:/stock/inbound/input";
	}
	
	@ExceptionHandler(NoStockException.class)
	public String handleNoStock(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.stock.data.notfound");
	    return "redirect:/stock/inbound/input";
	}
	
	@ExceptionHandler(StockOverException.class)
	public String handleStockOver(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.stock.data.inbound.over");
	    return "redirect:/stock/inbound/input";
	}
}
