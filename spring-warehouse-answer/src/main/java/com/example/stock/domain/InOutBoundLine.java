package com.example.stock.domain;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InOutBoundLine implements Serializable {
	public static String INBOUND = "入荷";
	public static String OUTBOUND = "出荷";
	
	private int no;
	private String inOutType;
	private int goodsCode;
	private int quantity;

	public InOutBoundLine(String inOutType, int goodsCode, int quantity) {
		this.inOutType = inOutType;
		this.goodsCode = goodsCode;
		this.quantity = quantity;
	}
	private static final long serialVersionUID = 1L;
}
