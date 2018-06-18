package com.example.goods.service;

import java.util.List;

import com.example.goods.domain.Goods;
import com.example.goods.exception.GoodsCodeDupulicateException;
import com.example.goods.exception.NoGoodsException;

public interface GoodsRepository {
	void createGoods(Goods goods) throws GoodsCodeDupulicateException;

	List<Goods> findAllGoods()  throws NoGoodsException;

	Goods findGoods(int goodsCode) throws NoGoodsException;

	void deleteGoods(int goodsCode) throws NoGoodsException;

	boolean isGoodsDeactive(int goodsCode);
}
