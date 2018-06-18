package com.example.goods.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.example.goods.domain.Goods;
import com.example.goods.exception.GoodsCodeDupulicateException;
import com.example.goods.exception.NoGoodsException;
import com.example.goods.service.GoodsRepository;

@Repository
public class GoodsRepositoryImpl implements GoodsRepository {

	@Autowired
	private GoodsMapper goodsMapper;

	public void createGoods(Goods goods) throws GoodsCodeDupulicateException {
		try {
			goodsMapper.createGoods(goods);
		} catch (DuplicateKeyException e) {
			throw new GoodsCodeDupulicateException();
		}
	}

	public List<Goods> findAllGoods() throws NoGoodsException {
		List<Goods> goodsList = goodsMapper.findAllGoods();
		if (goodsList.isEmpty())
			throw new NoGoodsException();
		return goodsList;
	}

	public Goods findGoods(int goodsCode) throws NoGoodsException {
		Goods goods = goodsMapper.findGoods(goodsCode);
		if (goods == null)
			throw new NoGoodsException();
		return goods;
	}

	@Override
	public void deleteGoods(int goodsCode) throws NoGoodsException {
		int count = goodsMapper.deleteGoods(goodsCode);
		if (count == 0) {
			throw new NoGoodsException();
		}
	}

	@Override
	public boolean isGoodsDeactive(int goodsCode) {
		int count = goodsMapper.getGoodsDeactiveCount(goodsCode);
		if (count == 0)
			return false;
		return true;
	}
}
