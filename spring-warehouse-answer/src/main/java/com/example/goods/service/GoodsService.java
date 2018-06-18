package com.example.goods.service;

import java.util.List;

import com.example.goods.domain.Goods;
import com.example.goods.exception.GoodsCodeDupulicateException;
import com.example.goods.exception.GoodsDeletedException;
import com.example.goods.exception.NoGoodsException;

public interface GoodsService {

	void createGoods(Goods goods) throws GoodsDeletedException, GoodsCodeDupulicateException;

	List<Goods> findAllGoods() throws NoGoodsException;

	Goods findGoods(int goodsCode) throws NoGoodsException;

	boolean isGoodsDeactive(int goodsCode);

	boolean canCreateGoods(int goodsCode) throws GoodsCodeDupulicateException, GoodsDeletedException;

	void deleteGoods(int goodsCode) throws GoodsDeletedException, NoGoodsException;

}