package com.example.stock.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.goods.exception.GoodsCodeDupulicateException;
import com.example.goods.exception.NoGoodsException;
import com.example.goods.service.GoodsService;
import com.example.stock.domain.InOutBoundLine;
import com.example.stock.domain.Stock;
import com.example.stock.exception.NoInOutBoundHistoryException;
import com.example.stock.exception.NoStockException;
import com.example.stock.exception.StockDeletedException;
import com.example.stock.exception.StockOverException;
import com.example.stock.exception.StockUnderException;

@Service
@Transactional(rollbackFor = Exception.class)
public class StockServiceImpl implements StockService {
	
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private StockRepository stockRepository;
	@Autowired
	private InOutBoundLineRepository inOutBoundLineRepository;

	public void createStock(Stock stock)
			throws GoodsCodeDupulicateException, NoGoodsException, StockDeletedException {
		goodsService.findGoods(stock.getGoodsCode());
		if(stockRepository.isStockDeactive(stock.getGoodsCode())) {
			throw new StockDeletedException();
		}
		if(stockRepository.findStock(stock.getGoodsCode()) != null) {
			throw new GoodsCodeDupulicateException();
		}
		stockRepository.createStock(stock);
	}

	@Transactional(readOnly = true)
	public Stock findStock(int goodsCode) throws NoStockException {
			Stock stock = stockRepository.findStock(goodsCode);
			if(stock == null) {
				throw new NoStockException();
			}
			return stock;
	}

	@Transactional(readOnly = true)
	public List<Stock> findAllStock() throws NoStockException {
			List<Stock> stockList = stockRepository.findAllStock();
			if(stockList == null) {
				throw new NoStockException();
			}
			return stockList;
	}

	public void deleteStock(int goodsCode) throws NoStockException, StockDeletedException {
				if(stockRepository.isStockDeactive(goodsCode)) {
					throw new StockDeletedException();
				}
				int cnt = stockRepository.deleteStock(goodsCode);
				if(cnt == 0) {
					throw new NoStockException();
				}
	}

	public Stock inBound(InOutBoundLine inOutBoundLine) throws NoStockException, StockOverException {
				Stock stock = findStock(inOutBoundLine.getGoodsCode());
				if(stock == null) {
					throw new NoStockException();
				}
				stock.incrementQuantity(inOutBoundLine.getQuantity());
				inOutBound(stock, inOutBoundLine);
				return stock;
	}

	public Stock outBound(InOutBoundLine inOutBoundLine) throws NoStockException, StockUnderException {
				Stock stock = findStock(inOutBoundLine.getGoodsCode());
				if(stock == null) {
					throw new NoStockException();
				}
				stock.decrementQuantity(inOutBoundLine.getQuantity());
				inOutBound(stock, inOutBoundLine);
				return stock;
	}

	private void inOutBound(Stock stock, InOutBoundLine inOutBoundLine) throws NoStockException {
		int cnt = stockRepository.updateStock(stock);
		if(cnt == 0) {
			throw new NoStockException();
		}
		inOutBoundLineRepository.createInOutBoundHistory(inOutBoundLine);
	}

	@Transactional(readOnly = true)
	public List<InOutBoundLine> findInOutBoundHistory() throws NoInOutBoundHistoryException {
			List<InOutBoundLine> inOutBoundLine = inOutBoundLineRepository.findInOutBoundHistory();
			if(inOutBoundLine.size() == 0) {
				throw new NoInOutBoundHistoryException();
			}
			return inOutBoundLine;
	}

	@Transactional(readOnly = true)
	public boolean isStockDeactive(int goodsCode) {
		return stockRepository.isStockDeactive(goodsCode);
	}

	@Transactional(readOnly = true)
	public boolean isStockCreate(int goodsCode) throws GoodsCodeDupulicateException {
		Stock stock = stockRepository.findStock(goodsCode);
		if(stock != null) {
			throw new GoodsCodeDupulicateException();
		}
		return !stockRepository.isStockDeactive(goodsCode);
	}
}
