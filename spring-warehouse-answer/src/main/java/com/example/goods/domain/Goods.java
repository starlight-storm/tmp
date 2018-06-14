package com.example.goods.domain;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Min(1)
	@Max(999)
	private Integer code;
	
	@NotBlank
	@Length(min=1, max=20)
	private String name;
	
	@NotNull
	@Min(10)
	@Max(9999)
	private Integer price;
}
