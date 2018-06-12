package com.example.goods.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.goods.domain.Goods;
import com.example.goods.exception.GoodsCodeDupulicateException;
import com.example.goods.exception.GoodsDeletedException;
import com.example.goods.exception.NoGoodsException;

@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private GoodsRepository goodsRepository;

	@Transactional
	public void createGoods(Goods goods) {
		if(goodsRepository.isGoodsDeactive(goods.getCode())) {
			throw new GoodsDeletedException();
		}
		goodsRepository.createGoods(goods);
	}

	@Transactional(readOnly=true)
	public List<Goods> findAllGoods() {
		return goodsRepository.findAllGoods();
	}

	@Transactional(readOnly=true)
	public Goods findGoods(int goodsCode) {
		return goodsRepository.findGoods(goodsCode);
	}

	@Transactional
	public void deleteGoods(int goodsCode) {
		if(goodsRepository.isGoodsDeactive(goodsCode)) {
			throw new GoodsDeletedException();
		}
		goodsRepository.deleteGoods(goodsCode);
	}

	@Transactional(readOnly=true)
	public boolean isGoodsDeactive(int goodsCode) {
			return goodsRepository.isGoodsDeactive(goodsCode);
	}

	@Transactional(readOnly=true)
	public boolean isGoodsCreate(int goodsCode) {
		try {
			goodsRepository.findGoods(goodsCode);
			throw new GoodsCodeDupulicateException();
		} catch (NoGoodsException e) {
			return !goodsRepository.isGoodsDeactive(goodsCode);
		}
	}
}
