package com.example.stock.service;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.stock.domain.Stock;

@Mapper
public interface StockRepository {

	@Insert("insert into STOCK(GOODS_CODE, QUANTITY, STATUS)"
			+ "values(#{goodsCode}, #{quantity}, 'ACTIVE')")
	void createStock(Stock stock);

	@Select("select * from STOCK where STATUS = 'ACTIVE'")
	List<Stock> findAllStock();
	
	@Select("select * from STOCK "
			+ " where GOODS_CODE = #{goodsCode} and STATUS = 'ACTIVE'")
	Stock findStock(int goodsCode);

	@Update("update STOCK set STATUS = 'DEACTIVE' "
			+ " where GOODS_CODE = #{goodsCode} and STATUS = 'ACTIVE'"
			+ "and QUANTITY = " + Stock.ZERO_QUANTITY)
	int deleteStock(int goodsCode);

	@Update("update STOCK set QUANTITY = #{quantity} "
			+ " where GOODS_CODE = #{goodsCode} and STATUS = 'ACTIVE'")
	int updateStock(Stock stock);

	@Select("select count(*) = 1 from STOCK "
			+ " where GOODS_CODE = #{goodsCode} and STATUS = 'DEACTIVE'")
	boolean isStockDeactive(int goodsCode);
}
