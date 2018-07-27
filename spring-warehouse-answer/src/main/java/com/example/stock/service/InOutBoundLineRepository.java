package com.example.stock.service;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.stock.domain.InOutBoundLine;

@Mapper
public interface InOutBoundLineRepository {
	
	@Insert("insert into IN_OUT_LOG(IN_OUT_TYPE, GOODS_CODE, QUANTITY) " 
			+ "values(#{inOutType}, #{goodsCode}, #{quantity})")
	void createInOutBoundHistory(InOutBoundLine inOutBoundLine);

	@Select("select NO, IN_OUT_TYPE, GOODS_CODE, QUANTITY from IN_OUT_LOG")
	List<InOutBoundLine> findInOutBoundHistory();
}
