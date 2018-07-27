package com.example.goods.service;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.goods.domain.Goods;

@Mapper
public interface GoodsRepository {

	@Insert("insert into GOODS(CODE, NAME, PRICE, STATUS) "
			+ "values(#{code}, #{name}, #{price}, 'ACTIVE')")
	void createGoods(Goods goods);

	@Select("select * from GOODS where STATUS = 'ACTIVE'")
	List<Goods> findAllGoods();

	@Select("select * from GOODS "
			+ " where CODE = #{goodsCode} and STATUS = 'ACTIVE'")
	Goods findGoods(int goodsCode);

	@Update("update GOODS set STATUS = 'DEACTIVE' "
			+ " where CODE = #{goodsCode} and STATUS = 'ACTIVE'")
	int deleteGoods(int goodsCode);

	@Select("select count(*) = 1 from GOODS "
			+ " where CODE = #{goodsCode} and STATUS = 'DEACTIVE'")
	boolean isGoodsDeactive(int goodsCode);
}







