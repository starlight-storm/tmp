package com.example.stock.web.modelattribute;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class OutBound {
	public static final int MIN_OUTBOUD = 1;
	public static final int MAX_OUTBOUND = 10;
	
	@Min(1)
	@Max(999)
	private Integer goodsCode;
	
	@Min(MIN_OUTBOUD)
	@Max(MAX_OUTBOUND)
	private Integer outBoundQuantity;
	
	private Integer afterOutBoundQuantity;
}
