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

import com.example.goods.exception.GoodsDeletedException;
import com.example.goods.exception.NoGoodsException;
import com.example.goods.web.modelattribute.GoodsCode;
import com.example.stock.domain.Stock;
import com.example.stock.exception.NoStockException;
import com.example.stock.exception.StockDeletedException;
import com.example.stock.exception.StockNotEmptyException;
import com.example.stock.service.StockService;


@Controller
@RequestMapping("/stock")
@SessionAttributes({"stock", "goodsCode"})
public class StockDeleteController {
	@Autowired
	private StockService stockService;

	@GetMapping("/delete/input")
	public String prepare(GoodsCode goodsCode) {
		return "/stock/stock_delete_input";
	}
	
	@PostMapping("/{code}/delete/confirm")
	public String confirm(@Valid GoodsCode goodsCode, Errors errors, Model model) 
			throws NoGoodsException, GoodsDeletedException, StockNotEmptyException, NoStockException, StockDeletedException {
		if (errors.hasErrors()) {
			return "/goods/goods_delete_input";
		}

		if(stockService.isStockDeactive(goodsCode.getCode())) {
			throw new StockDeletedException();
		}

		Stock stock = stockService.findStock(goodsCode.getCode());
		if (stock.isDelete()) {
			model.addAttribute("stock", stock);
		} else {
			throw new StockNotEmptyException();
		}
		return "redirect:/stock/{code}/delete/confirmed";
	}

	@GetMapping("/{code}/delete/confirmed")
	public String confirmed() {
		return "/stock/stock_delete_confirm";
	}
	
	@PostMapping(value="/{code}/delete/complete", params="delete")
	public String complete(Stock stock, Errors errors) 
			throws GoodsDeletedException, NoGoodsException, NoStockException, StockDeletedException {
		int goodsCode = stock.getGoodsCode();

		stockService.deleteStock(goodsCode);
		return "redirect:/stock/{code}/delete/completed";
	}

	@PostMapping(value="/{code}/delete/complete", params="back")
	public String back() {
		return "/stock/stock_delete_input";
	}

	@GetMapping("/{code}/delete/completed")
	public String completed(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "/stock/stock_delete_complete";
	}
	
	@ExceptionHandler(StockDeletedException.class)
	public String handleStockDeleted(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.stock.data.deleted");
	    return "redirect:/stock/delete/input";
	}
	
	@ExceptionHandler(NoStockException.class)
	public String handleGoodsCodeDuplicate(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.stock.data.notfound");
	    return "redirect:/stock/delete/input";
	}
	
	@ExceptionHandler(GoodsDeletedException.class)
	public String handleGoodsDeleted(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.deleted");
	    return "redirect:/stock/delete/input";
	}
	
	@ExceptionHandler(NoGoodsException.class)
	public String handleNoGoods(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.goods.data.notfound");
	    return "redirect:/stock/delete/input";
	}
	
	@ExceptionHandler(StockNotEmptyException.class)
	public String handleStockNotEmpty(RedirectAttributes redirectAttr) {
	    redirectAttr.addFlashAttribute("errorCode", "errors.stock.data.notempty");
	    return "redirect:/stock/delete/input";
	}
}
