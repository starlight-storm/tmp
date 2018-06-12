package com.example.goods.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.goods.domain.Goods;

@Mapper
public interface GoodsMapper {

	static final String ACTIVE = "ACTIVE";
	static final String DEACTIVE = "DEACTIVE";

	@Insert("INSERT INTO GOODS(CODE, NAME, PRICE, STATUS) VALUES(#{code}, #{name}, #{price}, '" + ACTIVE + "')")
	void createGoods(Goods goods);

	@Select("SELECT CODE, NAME, PRICE FROM GOODS WHERE STATUS = '" + ACTIVE + "'")
	List<Goods> findAllGoods();
	
	@Select("SELECT CODE, NAME, PRICE FROM GOODS WHERE CODE = #{goodsCode} AND STATUS = '" + ACTIVE + "'")
	Goods findGoods(int goodsCode);

	@Update("UPDATE GOODS SET STATUS = '" + DEACTIVE + "'" + " WHERE  CODE = #{goodsCode} AND STATUS = '" + ACTIVE + "'")
	int deleteGoods(int goodsCode);

	@Select("SELECT COUNT(*) FROM GOODS WHERE CODE = #{goodsCode} AND STATUS = '" + DEACTIVE + "'")
	int getGoodsDeactiveCount(int goodsCode);
}
