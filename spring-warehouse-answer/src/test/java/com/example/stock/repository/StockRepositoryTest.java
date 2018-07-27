package com.example.stock.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import com.example.stock.domain.Stock;
import com.example.stock.service.StockRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/data/stock/INPUT_STOCK_DATA.xml")
@Transactional
public class StockRepositoryTest {
	@Autowired
	private StockRepository stockRepository;

	@Test
	public void testFindStock_正常系() throws Exception {
		Stock stock = stockRepository.findStock(0);
		assertEquals(new Integer(0), stock.getGoodsCode());
		assertEquals(new Integer(80), stock.getQuantity());
	}

	@Test
	public void testFindStock_異常系_存在しない商品コード() throws Exception {
		assertNull(stockRepository.findStock(9));
	}

	@Test
	public void testFindAllStock_正常系() throws Exception {
		List<Stock> stockList = stockRepository.findAllStock();

		if (stockList.size() != 4) fail();

		Stock stock = stockList.get(0);
		assertEquals(new Integer(0), stock.getGoodsCode());
		assertEquals(new Integer(80), stock.getQuantity());
		stock = stockList.get(1);
		assertEquals(new Integer(1), stock.getGoodsCode());
		assertEquals(new Integer(20), stock.getQuantity());
		stock = stockList.get(2);
		assertEquals(new Integer(3), stock.getGoodsCode());
		assertEquals(new Integer(0), stock.getQuantity());
		stock = stockList.get(3);
		assertEquals(new Integer(5), stock.getGoodsCode());
		assertEquals(new Integer(3), stock.getQuantity());
	}

	@Test
	@DatabaseSetup("/data/stock/INPUT_STOCK_NO_DATA.xml")
	public void testFindAllStock_異常系_1件もない() throws Exception {
		List<Stock> stockList = stockRepository.findAllStock();
		assertEquals(0, stockList.size());
	}

	@Test
	@ExpectedDatabase(
			value = "/data/stock/EXPECTED_CREATE_STOCK_DATA.xml",
			assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testCreateStock_正常系() throws Exception {
		Stock stock = new Stock(2);
		stockRepository.createStock(stock);
	}

	@Test
	public void testCreateStock_異常系_商品コードの重複() throws Exception {

		Stock stock = new Stock(0);

		try {
			stockRepository.createStock(stock);
			fail("Exception not thrown.");
		} catch (DuplicateKeyException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testCreateStock_異常系_削除済みの商品コードの重複() throws Exception {

		Stock stock = new Stock(4);

		try {
			stockRepository.createStock(stock);
			fail("Exception not thrown.");
		} catch (DuplicateKeyException e) {
			assertTrue(true);
		}
	}

	@Test
	@ExpectedDatabase(
			value = "/data/stock/EXPECTED_DELETE_STOCK_DATA.xml",
			assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testDeleteStock_正常系() throws Exception {

		int cnt = stockRepository.deleteStock(3);
		assertEquals(1, cnt);
	}

	@Test
	public void testDeleteStock_異常系_在庫が存在する商品コード() throws Exception {
		int cnt = stockRepository.deleteStock(1);
		assertEquals(0, cnt);
	}

	@Test
	public void testDeleteStock_異常系_存在しない商品コード() throws Exception {
		int cnt = stockRepository.deleteStock(222);
		assertEquals(0, cnt);
	}

	@Test
	public void testDeleteStock_異常系_削除済みの商品コード() throws Exception {
		int cnt = stockRepository.deleteStock(4);
		assertEquals(0, cnt);
	}

	@Test
	@ExpectedDatabase(
			value = "/data/stock/EXPECTED_UPDATE_STOCK_DATA.xml",
			assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testUpdateStock_正常系() throws Exception {

		Stock stock = new Stock(3, 99);
		int cnt = stockRepository.updateStock(stock);
		assertEquals(1, cnt);
	}

	@Test
	public void testUpdateStock_異常系_存在しない商品コード() throws Exception {
		Stock stock = new Stock(200, 99);
		int cnt = stockRepository.updateStock(stock);
		assertEquals(0, cnt);
	}

	@Test
	public void testUpdateStock_異常系_削除済みの商品コード() throws Exception {
		Stock stock = new Stock(4, 99);
		int cnt = stockRepository.updateStock(stock);
		assertEquals(0, cnt);
	}

	@Test
	public void testIsDeleteStock_正常系_未削除() throws SQLException {
		boolean ans = stockRepository.isStockDeactive(0);
		if (!ans) {
			assertTrue(true);
		} else {
			fail("存在しないコードが削除された.");
		}
	}

	@Test
	public void testIsDeleteStock_正常系_削除済() throws SQLException {
		boolean ans = stockRepository.isStockDeactive(4);
		if (ans) {
			assertTrue(true);
		} else {
			fail("存在しないコードが削除された.");
		}
	}

}