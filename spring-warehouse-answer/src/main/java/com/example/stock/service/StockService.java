package com.example.stock.service;

import java.util.List;

import com.example.goods.exception.GoodsCodeDupulicateException;
import com.example.goods.exception.NoGoodsException;
import com.example.stock.domain.InOutBoundLine;
import com.example.stock.domain.Stock;
import com.example.stock.exception.NoInOutBoundHistoryException;
import com.example.stock.exception.NoStockException;
import com.example.stock.exception.StockDeletedException;
import com.example.stock.exception.StockOverException;
import com.example.stock.exception.StockUnderException;

public interface StockService {

	void createStock(Stock stock)
			throws GoodsCodeDupulicateException, NoGoodsException, StockDeletedException;

	List<Stock> findAllStock() throws NoStockException;

	Stock findStock(int goodsCode) throws NoStockException;

	void deleteStock(int goodsCode) throws NoStockException, StockDeletedException;

	Stock inBound(InOutBoundLine inOutBoundLine) throws NoStockException, StockOverException;

	Stock outBound(InOutBoundLine inOutBoundLine) throws NoStockException, StockUnderException;

	List<InOutBoundLine> findInOutBoundHistory() throws NoInOutBoundHistoryException;

	boolean isStockDeactive(int goodsCode);

	boolean isStockCreate(int goodsCode) throws GoodsCodeDupulicateException;

}
