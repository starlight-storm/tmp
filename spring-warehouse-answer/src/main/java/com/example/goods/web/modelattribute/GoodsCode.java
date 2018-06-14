package com.example.goods.web.modelattribute;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class GoodsCode {
	@NotNull
	@Min(1)
	@Max(1000)
	private Integer code;
}