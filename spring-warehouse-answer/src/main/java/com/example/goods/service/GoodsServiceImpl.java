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
@Transactional(rollbackFor = Exception.class)
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private GoodsRepository goodsRepository;

	public void createGoods(Goods goods) throws GoodsDeletedException, GoodsCodeDupulicateException {
		if (goodsRepository.isGoodsDeactive(goods.getCode())) {
			throw new GoodsDeletedException();
		}

		if (goodsRepository.findGoods(goods.getCode()) != null) {
			throw new GoodsCodeDupulicateException();
		}

		goodsRepository.createGoods(goods);
	}

	@Transactional(readOnly = true)
	public List<Goods> findAllGoods() throws NoGoodsException {
		List<Goods> list = goodsRepository.findAllGoods();

		if (list.isEmpty()) {
			throw new NoGoodsException();
		}

		return list;
	}

	@Transactional(readOnly = true)
	public Goods findGoods(int goodsCode) throws NoGoodsException {
		Goods goods = goodsRepository.findGoods(goodsCode);

		if (goods == null) {
			throw new NoGoodsException();
		}

		return goods;
	}

	public void deleteGoods(int goodsCode) throws GoodsDeletedException, NoGoodsException {
		if (goodsRepository.isGoodsDeactive(goodsCode)) {
			throw new GoodsDeletedException();
		}

		int cnt = goodsRepository.deleteGoods(goodsCode);

		if (cnt == 0) {
			throw new NoGoodsException();
		}
	}

	@Transactional(readOnly = true)
	public boolean isGoodsDeactive(int goodsCode) {
		return goodsRepository.isGoodsDeactive(goodsCode);
	}

	@Transactional(readOnly = true)
	public void checkGoodsCanCreate(int goodsCode) throws GoodsCodeDupulicateException, GoodsDeletedException {
		Goods goods = goodsRepository.findGoods(goodsCode);
		if (goods != null) {
			throw new GoodsCodeDupulicateException();
		}

		if (goodsRepository.isGoodsDeactive(goodsCode)) {
			throw new GoodsDeletedException();
		}
		return;
	}
}
