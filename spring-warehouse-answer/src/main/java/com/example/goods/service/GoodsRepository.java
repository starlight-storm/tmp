package com.example.goods.service;

import java.util.List;

import com.example.goods.domain.Goods;

public interface GoodsRepository {
	void createGoods(Goods goods);
	List<Goods> findAllGoods();
	Goods findGoods(int goodsCode);
	void deleteGoods(int goodsCode);
	boolean isGoodsDeactive(int goodsCode);
}
