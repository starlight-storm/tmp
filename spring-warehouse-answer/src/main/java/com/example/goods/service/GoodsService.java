package com.example.goods.service;

import java.util.List;

import com.example.goods.domain.Goods;

public interface GoodsService {

	void createGoods(Goods goods);

	List<Goods> findAllGoods();

	Goods findGoods(int goodsCode);

	boolean isGoodsDeactive(int goodsCode);

	boolean isGoodsCreate(int goodsCode);

	void deleteGoods(int goodsCode);

}