package com.example.goods.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.example.goods.domain.Goods;
import com.example.goods.exception.GoodsCodeDupulicateException;
import com.example.goods.exception.GoodsDeletedException;
import com.example.goods.exception.NoGoodsException;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/data/goods/INPUT_GOODS_DATA.xml")
public class GoodsServiceTest {

	@Autowired
	private GoodsService goodsService;

	@Test
	public void testFindGoods_正常系() throws Exception {
		Goods goods = goodsService.findGoods(1);

		assertEquals(new Integer(1), goods.getCode());
		assertEquals("いちご", goods.getName());
		assertEquals(new Integer(350), goods.getPrice());
	}

	@Test
	public void testFindGoods_異常系_存在しない商品コード() {
		try {
			goodsService.findGoods(777);
			fail("Exception not thrown.");
		} catch (NoGoodsException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testFindAllGoods_正常系() throws Exception {
		List<Goods> goodsList = goodsService.findAllGoods();

		if (goodsList.size() != 5)
			fail("正しいサイズではない.");

		Goods goods = goodsList.get(0);
		assertEquals(new Integer(0), goods.getCode());
		assertEquals("りんご", goods.getName());
		assertEquals(new Integer(100), goods.getPrice());
		goods = goodsList.get(1);
		assertEquals(new Integer(1), goods.getCode());
		assertEquals("いちご", goods.getName());
		assertEquals(new Integer(350), goods.getPrice());
		goods = goodsList.get(2);
		assertEquals(new Integer(2), goods.getCode());
		assertEquals("白菜", goods.getName());
		assertEquals(new Integer(90), goods.getPrice());
		goods = goodsList.get(3);
		assertEquals(new Integer(6), goods.getCode());
		assertEquals("クレヨン", goods.getName());
		assertEquals(new Integer(1280), goods.getPrice());
		goods = goodsList.get(4);
		assertEquals(new Integer(7), goods.getCode());
		assertEquals("サインペン", goods.getName());
		assertEquals(new Integer(50), goods.getPrice());
	}

	@Test
	@DatabaseSetup("/data/goods/INPUT_GOODS_NO_DATA.xml")
	public void testFindAllGoods_異常系_1件もない() throws Exception {
		try {
			List<Goods> goodsList = goodsService.findAllGoods();
			for (Goods goods : goodsList) {
				System.out.println(goods);
			}
			fail("Exception not thrown.");
		} catch (NoGoodsException e) {
			assertTrue(true);
		}
	}

	@Test
	@ExpectedDatabase("/data/goods/EXPECTED_CREATE_GOODS_DATA.xml")
	public void testCreateGoods_正常系() throws Exception {
		Goods goods = new Goods(99, "バナナ", 210);
		goodsService.createGoods(goods);
	}

	@Test
	public void testCreateGoods_異常系_商品コードの重複() throws Exception {

		Goods goods = new Goods(0, "イチジク", 210);

		try {
			goodsService.createGoods(goods);
			fail("Exception not thrown.");
		} catch (GoodsCodeDupulicateException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testCreateGoods_異常系_削除済みの商品コードの重複() throws GoodsCodeDupulicateException {

		Goods goods = new Goods(3, "イチジク", 210);

		try {
			goodsService.createGoods(goods);
			fail("Exception not thrown.");
		} catch (GoodsDeletedException e) {
			assertTrue(true);
		}
	}

	@Test
	@ExpectedDatabase("/data/goods/EXPECTED_DELETE_GOODS_DATA.xml")
	public void testDeleteGoods_正常系() throws Exception {
		goodsService.deleteGoods(1);
	}

	@Test
	public void testDeleteGoods_異常系_存在しない商品コード() throws GoodsDeletedException {
		try {
			goodsService.deleteGoods(1001);
			fail("Exception not thrown.");
		} catch (NoGoodsException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testDeleteGoods_異常系_削除済みの商品コード() throws NoGoodsException {
		try {
			goodsService.deleteGoods(3);
			fail("Exception not thrown.");
		} catch (GoodsDeletedException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testIsDeactiveGoods_正常系_削除済み() {
		if (goodsService.isGoodsDeactive(4)) {
			assertTrue(true);
		} else {
			fail("削除できない.");
		}
	}

	@Test
	public void testIsDeactiveGoods_正常系_コードが存在しない() {
		if (!goodsService.isGoodsDeactive(9)) {
			assertTrue(true);
		} else {
			fail("存在しないコードが削除された.");
		}
	}

	@Test
	public void testCanCreateGoods_正常系() throws GoodsCodeDupulicateException, GoodsDeletedException {
		goodsService.checkGoodsCanCreate(8);
		assertTrue(true);
	}

	@Test
	public void testCanCreateGoods_異常系_削除済みの商品コード() throws GoodsCodeDupulicateException {
		try {
			goodsService.checkGoodsCanCreate(3);
			fail("Exception not thrown.");
		} catch (GoodsDeletedException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testCanCreateGoods_異常系_登録済みの商品コード() throws GoodsDeletedException {
		try {
			goodsService.checkGoodsCanCreate(0);
			fail("Exception not thrown.");
		} catch (GoodsCodeDupulicateException e) {
			assertTrue(true);
		}
	}
}