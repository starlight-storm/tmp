package com.example.stock.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.goods.exception.NoGoodsException;
import com.example.stock.domain.Stock;
import com.example.stock.exception.NoStockException;
import com.example.stock.service.StockService;


@Controller
@RequestMapping("/stock")
public class StockListController {
	@Autowired
	StockService stockService;

private static int PAGE_SIZE = 3;
	
	@GetMapping("/find/list")
	public String show(@RequestParam(value = "page", defaultValue = "0") String page, Model model) throws NoGoodsException, NoStockException {		
		PagedListHolder<Stock> stockList = new PagedListHolder<>(stockService.findAllStock());
		stockList.setPage(Integer.parseInt(page));
		stockList.setPageSize(PAGE_SIZE);
		model.addAttribute("stockList", stockList);
		return "/stock/stock_list";
	}

	@ExceptionHandler(NoGoodsException.class)
	public String handlerNoGoods(Model model) {
		model.addAttribute("stockList", null);
		return "/stock/stock_list";
	}
	
	@ExceptionHandler(NoStockException.class)
	public String handlerNoStock(Model model) {
		model.addAttribute("stockList", null);
		return "/stock/stock_list";
	}
}
