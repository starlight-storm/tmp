package com.example.stock.web.modelattribute;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class InBound {
	public static final int MIN_INBOUD = 1;
	public static final int MAX_INBOUND = 10;
	
	@Min(1)
	@Max(999)
	private Integer goodsCode;
	
	@Min(MIN_INBOUD)
	@Max(MAX_INBOUND)
	private Integer inBoundQuantity;
	
	private Integer afterInBoundQuantity;
}
