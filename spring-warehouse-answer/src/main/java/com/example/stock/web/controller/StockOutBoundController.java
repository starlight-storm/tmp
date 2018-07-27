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
import com.example.stock.exception.StockUnderException;
import com.example.stock.service.StockService;
import com.example.stock.web.modelattribute.OutBound;

@Controller
@RequestMapping("/stock")
@SessionAttributes("outBound")
public class StockOutBoundController {
	@Autowired
	StockService stockService;
	
	@GetMapping("/outbound/input")
	public String prepare(OutBound outBound, Model model) {
		model.addAttribute("outBound", outBound);
		return "/stock/stock_outbound_input";
	}

	@PostMapping("/{code}/outbound/confirm")
	public String confirm(@Valid OutBound outBound, Errors errors, Model model) 
			throws NoStockException, StockUnderException 
			 {
		if (errors.hasErrors()) {
			return "/stock/stock_outbound_input";
		}
		
		int goodsCode = outBound.getGoodsCode();
		int outBoundQuantity = outBound.getOutBoundQuantity();
		
		Stock masterStock = stockService.findStock(goodsCode);
		masterStock.decrementQuantity(outBoundQuantity);
		
		return "redirect:/stock/{code}/outbound/confirmed";
	}

	@GetMapping("/{code}/outbound/confirmed")
	public String confirmed() {
		return "/stock/stock_outbound_confirm";
	}
	
	@PostMapping(value="/{code}/outbound/complete", params="outbound")
	public String complete(OutBound outBound) 
			throws NoStockException, StockUnderException {
		
		int goodsCode = outBound.getGoodsCode();
		int outBoundQuantity = outBound.getOutBoundQuantity();
		
		InOutBoundLine inOutBoundLine = new InOutBoundLine(InOutBoundLine.OUTBOUND, goodsCode, outBoundQuantity);
		
		Stock stock = stockService.outBound(inOutBoundLine);
		Integer afterOutBoundQuantity = stock.getQuantity();
		outBound.setAfterOutBoundQuantity(afterOutBoundQuantity);
		
		return "redirect:/stock/{code}/outbound/completed";
	}

	@PostMapping(value="/{code}/outbound/complete", params="back")
	public String back() {
		return "/stock/stock_outbound_input";
	}

	@GetMapping("/{code}/outbound/completed")
	public String completed(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "/stock/stock_outbound_complete";
	}
	
	@ExceptionHandler(StockDeletedException.class)
	public String handleStockDeleted(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.stock.data.deleted");
	    return "redirect:/stock/outbound/input";
	}
	
	@ExceptionHandler(GoodsCodeDupulicateException.class)
	public String handleGoodsCodeDuplicate(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.duplicate");
	    return "redirect:/stock/outbound/input";
	}
	
	@ExceptionHandler(NoStockException.class)
	public String handleNoStock(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.stock.data.notfound");
	    return "redirect:/stock/outbound/input";
	}
	
	@ExceptionHandler(StockUnderException.class)
	public String handleStockOver(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.stock.data.outbound.under");
	    return "redirect:/stock/outbound/input";
	}
}
