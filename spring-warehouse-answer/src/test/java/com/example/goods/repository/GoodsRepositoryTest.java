package com.example.goods.repository;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.example.goods.domain.Goods;
import com.example.goods.service.GoodsRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/data/goods/INPUT_GOODS_DATA.xml")
@Transactional
public class GoodsRepositoryTest {

	@Autowired
	private GoodsRepository goodsRepository;

	@Test
	public void testFindGoods_正常系() throws Exception {
		Goods goods = goodsRepository.findGoods(1);

		assertEquals(new Integer(1), goods.getCode());
		assertEquals("いちご", goods.getName());
		assertEquals(new Integer(350), goods.getPrice());
	}

	@Test
	public void testFindGoods_異常系_存在しない商品コード() {
		assertNull(goodsRepository.findGoods(777));
	}

	@Test
	public void testFindGoods_異常系_削除済みの商品コード() {
		assertNull(goodsRepository.findGoods(3));
	}

	@Test
	public void testFindAllGoods_正常系() throws Exception {
		List<Goods> goodsList = goodsRepository.findAllGoods();

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
		List<Goods> goodsList = goodsRepository.findAllGoods();
		assertEquals(0, goodsList.size());
	}

	@Test
	@ExpectedDatabase(
			value = "/data/goods/EXPECTED_CREATE_GOODS_DATA.xml",
			assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testCreateGoods_正常系() throws Exception {
		Goods goods = new Goods(99, "バナナ", 210);
		goodsRepository.createGoods(goods);
	}

	@Test
	public void testCreateGoods_異常系_商品コードの重複() {

		Goods goods = new Goods(0, "イチジク", 210);

		try {
			goodsRepository.createGoods(goods);
			fail("Exception not thrown.");
		} catch (DuplicateKeyException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testCreateGoods_異常系_削除済みの商品コードの重複() {

		Goods goods = new Goods(2, "イチジク", 210);

		try {
			goodsRepository.createGoods(goods);
			fail("Exception not thrown.");
		} catch (DuplicateKeyException e) {
			assertTrue(true);
		}
	}

	@Test
	@ExpectedDatabase(
			value = "/data/goods/EXPECTED_DELETE_GOODS_DATA.xml",
			assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testDeleteGoods_正常系() throws Exception {
		int cnt = goodsRepository.deleteGoods(1);
		assertEquals(1, cnt);
	}

	@Test
	public void testDeleteGoods_異常系_存在しない商品コード() {
		int cnt = goodsRepository.deleteGoods(1001);
		assertEquals(0, cnt);
	}

	@Test
	public void testDeleteGoods_異常系_削除済みの商品コード() {
		int cnt = goodsRepository.deleteGoods(3);
		assertEquals(0, cnt);
	}

	@Test
	public void testIsDeleteGoods_正常系_未削除() throws SQLException {
		boolean ans = goodsRepository.isGoodsDeactive(0);
		if (!ans) {
			assertTrue(true);
		} else {
			fail("存在しないコードが削除された.");
		}
	}

	@Test
	public void testIsDeleteGoods_正常系_削除済() throws SQLException {
		boolean ans = goodsRepository.isGoodsDeactive(4);
		if (ans) {
			assertTrue(true);
		} else {
			fail("削除できない.");
		}
	}
}