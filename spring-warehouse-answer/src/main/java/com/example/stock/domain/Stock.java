package com.example.stock.domain;

import java.io.Serializable;

import com.example.stock.exception.StockOverException;
import com.example.stock.exception.StockUnderException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock implements Serializable {

	public static final int ZERO_QUANTITY = 0;
	public static final int MAX_STOCK = 100;
	public static final int MIN_STOCK = 0;
	
	private Integer goodsCode;
	private Integer quantity;
	
	public Stock(int goodsCode) {
		this.goodsCode = goodsCode;
		this.quantity = ZERO_QUANTITY;
	}

	public void incrementQuantity(int quantity) throws StockOverException {
		this.quantity = this.quantity + quantity;
		if (this.quantity > MAX_STOCK) throw new StockOverException();
	}

	public void decrementQuantity(int quantity) throws StockUnderException {
		this.quantity = this.quantity - quantity;
		if (this.quantity < MIN_STOCK) throw new StockUnderException();
	}

	public boolean isDelete() {
		if (this.quantity == MIN_STOCK) return true;
		return false;
	}

	private static final long serialVersionUID = 1L;
}
