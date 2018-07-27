package com.example.stock.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.stock.domain.InOutBoundLine;
import com.example.stock.exception.NoInOutBoundHistoryException;
import com.example.stock.service.StockService;


@Controller
public class InOutBoundHistoryController {
	
	@Autowired
	private StockService stockService;
    
	private static int PAGE_SIZE = 3;
	
    @GetMapping("/inoutbound/history")
	public String show(@RequestParam(value = "page", defaultValue = "0") String page, Model model) 
			throws NoInOutBoundHistoryException {		
		PagedListHolder<InOutBoundLine> inOutBoundLineList = new PagedListHolder<>(stockService.findInOutBoundHistory());
		inOutBoundLineList.setPage(Integer.parseInt(page));
		inOutBoundLineList.setPageSize(PAGE_SIZE);
		model.addAttribute("inOutBoundLineList", inOutBoundLineList);
		return "/stock/inoutbound_history";
	}
    
	@ExceptionHandler(NoInOutBoundHistoryException.class)
	public String handlerNoInOutBoundHistory(Model model) {
		model.addAttribute("inOutBoundLineList", null);
		return "/stock/inoutbound_history";
	}
}
