package com.example.goods.web.modelattribute;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class GoodsCode {
	@Min(1)
	@Max(1000)
	private Integer code;
}