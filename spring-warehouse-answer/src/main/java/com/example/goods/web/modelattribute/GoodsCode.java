package com.example.goods.web.modelattribute;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class GoodsCode {
	@Min(1)
	@Max(1000)
	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}