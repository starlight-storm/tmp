package com.example.stock.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.springframework.transaction.annotation.Transactional;

import com.example.goods.exception.GoodsCodeDupulicateException;
import com.example.goods.exception.NoGoodsException;
import com.example.stock.domain.InOutBoundLine;
import com.example.stock.domain.Stock;
import com.example.stock.exception.NoInOutBoundHistoryException;
import com.example.stock.exception.NoStockException;
import com.example.stock.exception.StockDeletedException;
import com.example.stock.exception.StockOverException;
import com.example.stock.exception.StockUnderException;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/data/stock/INPUT_SERVICE_DATA.xml")
@Transactional
public class StockServiceTest {

	@Autowired
	private StockService stockService;

	@Test
	public void testFindStock_正常系() throws Exception {
		Stock stock = stockService.findStock(0);
		assertEquals(new Integer(0), stock.getGoodsCode());
		assertEquals(new Integer(80), stock.getQuantity());
	}

	@Test
	public void testFindStock_異常系_存在しない商品コード() {
		try {
			stockService.findStock(777);
			fail();
		} catch (NoStockException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testFindAllStock_正常系() throws Exception {
		List<Stock> stockList = stockService.findAllStock();

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
	@DatabaseSetup("/data/stock/INPUT_SERVICE_NO_DATA.xml")
	public void testFindAllStock_異常系_1件もない() throws Exception {
		List<Stock> stockList = stockService.findAllStock();
		assertEquals(0, stockList.size());
	}

	@Test
	@ExpectedDatabase(
			value = "/data/stock/EXPECTED_CREATE_STOCK_DATA.xml",
			assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testCreateStock_正常系() throws Exception {
		Stock stock = new Stock(2);
		stockService.createStock(stock);
	}

	@Test
	public void testCreateStock_異常系_商品コードの重複() {
		Stock stock = new Stock(0);

		try {
			stockService.createStock(stock);
			fail("GoodsCodeDupulicateExceptionが出る予定が、正常終了してしまった.");
		} catch (GoodsCodeDupulicateException e) {
			assertTrue(true);
		} catch (NoGoodsException | StockDeletedException e) {
			fail("GoodsCodeDupulicateExceptionとは異なる例外が出力された.");
		}
	}

	@Test
	public void testCreateStock_異常系_商品テーブルで削除済みの商品コード() {
		Stock stock = new Stock(4);
		try {
			stockService.createStock(stock);
			fail("NoGoodsExceptionが出る予定が、正常終了してしまった.");
		} catch (NoGoodsException e) {
			assertTrue(true);
		} catch (GoodsCodeDupulicateException | StockDeletedException e) {
			fail("NoGoodsExceptionとは異なる例外が出力された.");
		}
	}

	@Test
	public void testCreateStock_異常系_在庫テーブルで削除済みの商品コード() {
		Stock stock = new Stock(7);
		try {
			stockService.createStock(stock);
			fail("StockDeletedExceptionが出る予定が、正常終了してしまった.");
		} catch (StockDeletedException e) {
			assertTrue(true);
		} catch (GoodsCodeDupulicateException | NoGoodsException e) {
			fail("StockDeletedExceptionとは異なる例外が出力された.");
		}
	}

	@Test
	@ExpectedDatabase(
			value = "/data/stock/EXPECTED_DELETE_STOCK_DATA.xml",
			assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testDeleteStock_正常系() throws Exception {
		Stock stock = stockService.findStock(3);
		if(stock.isDelete()) {
			stockService.deleteStock(3);
		} else {
			fail("削除できなかった.");
		}
	}

	@Test
	public void testDeleteStock_異常系_在庫が存在するため削除できない_isDelete() throws Exception {
		Stock stock = stockService.findStock(0);
		assertFalse(stock.isDelete());
	}

	@Test
	public void testDeleteStock_異常系_在庫が存在するため削除できない_deleteStock() {
		try {
			stockService.deleteStock(0);
		} catch(NoStockException e) {
			assertTrue(true);
		} catch (StockDeletedException e) {
			fail("存在しているはずの在庫がない.");
		}
	}

	@Test
	public void testDeleteStock_異常系_存在しない商品コード() {
		try {
			stockService.deleteStock(2);
		} catch (NoStockException e) {
			assertTrue(true);
		} catch (StockDeletedException e) {
			fail("在庫がないはずなのに、存在していた.");
		}
	}

	@Test
	public void testDeleteStock_異常系_削除済みの商品コード() {
		try {
			stockService.deleteStock(4);
		} catch (StockDeletedException e) {
			assertTrue(true);
		} catch (NoStockException e) {
			fail("削除済みの商品コードが削除されていなかった.");
		}
	}

	@Test
	public void testFindInOutHistory_正常系() throws Exception {
		List<InOutBoundLine> inOutBoundLineList = stockService.findInOutBoundHistory();

		if (inOutBoundLineList.size() != 4) fail();

		InOutBoundLine log = inOutBoundLineList.get(0);
		assertEquals("出荷", log.getInOutType());
		assertEquals(30, log.getGoodsCode());
		assertEquals(80, log.getQuantity());

		log = inOutBoundLineList.get(1);
		assertEquals("入荷", log.getInOutType());
		assertEquals(100, log.getGoodsCode());
		assertEquals(20, log.getQuantity());

		log = inOutBoundLineList.get(2);
		assertEquals("出荷", log.getInOutType());
		assertEquals(222, log.getGoodsCode());
		assertEquals(4, log.getQuantity());

		log = inOutBoundLineList.get(3);
		assertEquals("出荷", log.getInOutType());
		assertEquals(100, log.getGoodsCode());
		assertEquals(6, log.getQuantity());
	}

	@Test
	@DatabaseSetup("/data/stock/INPUT_INOUTBOUND_NO_DATA.xml")
	public void testFindInOutBoundHistory_異常系_1件もない() throws Exception {
		try {
			stockService.findInOutBoundHistory();
			fail("データがないはずなのに、存在していた.");
		} catch (NoInOutBoundHistoryException e) {
			assertTrue(true);
		}
	}

	@Test
	@ExpectedDatabase(
			value = "/data/stock/EXPECTED_SERVICE_STOCK_DATA1.xml",
			assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testOutBound_正常系_出荷_Stock() throws Exception {
		InOutBoundLine inOutBoundLine = new InOutBoundLine("出荷", 0, 12);

		Stock expectedStock = stockService.outBound(inOutBoundLine);
		assertEquals(expectedStock, new Stock(0, 68));
	}
	
	@Test
	public void testOutBound_正常系_出荷_InOutBound() throws Exception {
		InOutBoundLine inOutBoundLine = new InOutBoundLine("出荷", 0, 12);

		Stock expectedStock = stockService.outBound(inOutBoundLine);
		assertEquals(expectedStock, new Stock(0, 68));
	}

	@Test
	public void testOutBound_異常系_存在しない商品コード() {
		try {
			InOutBoundLine inOutBoundLine = new InOutBoundLine("出荷", 200, 10);
			stockService.inBound(inOutBoundLine);
			fail("NoStockExceptionが出る予定が、正常終了してしまった.");
		} catch (NoStockException e) {
			assertTrue(true);
		} catch (StockOverException e) {
			fail("NoStockExceptionが出る予定が、StockOverExceptionが出てしまった.");
		}
	}

	@Test
	public void testOutBound_異常系_削除済みの商品コード() {
		try {
			InOutBoundLine inOutBoundLine = new InOutBoundLine("出荷", 4, 10);
			stockService.inBound(inOutBoundLine);
			fail("NoStockExceptionが出る予定が、正常終了してしまった.");
		} catch (NoStockException e) {
			assertTrue(true);
		} catch (StockOverException e) {
			fail("NoStockExceptionが出る予定が、StockOverExceptionが出てしまった.");
		}
	}

	@Test
	@ExpectedDatabase(
			value = "/data/stock/EXPECTED_SERVICE_STOCK_DATA2.xml",
			assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testInBound_正常系_入荷_Stock() throws Exception {
		InOutBoundLine inOutBoundLine = new InOutBoundLine("入荷", 0, 7);
		Stock expectedStock = stockService.inBound(inOutBoundLine);
		assertEquals(expectedStock, new Stock(0, 87));
	}
	
	@Test
	@ExpectedDatabase(
			value = "/data/stock/EXPECTED_SERVICE_INOUTBOUND_DATA2.xml",
			assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testInBound_正常系_入荷_InOutBound() throws Exception {
		InOutBoundLine inOutBoundLine = new InOutBoundLine("入荷", 0, 7);
		Stock expectedStock = stockService.inBound(inOutBoundLine);
		assertEquals(expectedStock, new Stock(0, 87));
	}

	@Test
	public void testInBound_異常系_存在しない商品コード() {
		try {
			InOutBoundLine inOutBoundLine = new InOutBoundLine("入荷", 200, 10);
			stockService.inBound(inOutBoundLine);
			fail("存在しない商品コードが存在した.");
		} catch (NoStockException e) {
			assertTrue(true);
		} catch (StockOverException e) {
			fail("NoStockExceptionが出る予定が、StockOverExceptionが出てしまった.");
		}
	}

	@Test
	public void testInBound_異常系_削除済みの商品コード() {
		try {
			InOutBoundLine inOutBoundLine = new InOutBoundLine("入荷", 4, 10);
			stockService.inBound(inOutBoundLine);
			fail("削除済みの商品コードが存在した.");
		} catch (NoStockException e) {
			assertTrue(true);
		} catch (StockOverException e) {
			fail("NoStockExceptionが出る予定が、StockOverExceptionが出てしまった.");
		}
	}

	@Test
	public void testOutBound_異常系_出荷数オーバー() {
		Stock stock = new Stock(6);
		try {
			stock.decrementQuantity(10);
			fail("出荷数がオーバーしなかった.");
		} catch (StockUnderException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testOutBound_異常系_出荷数オーバー_Serviceチェック() {
		try {
			InOutBoundLine inOutBoundLine = new InOutBoundLine("入荷", 1, 30);
			stockService.outBound(inOutBoundLine);
			fail("出荷数がオーバーしなかった.");
		} catch (StockUnderException e) {
			assertTrue(true);
		} catch (NoStockException e) {
			fail("StockOverExceptionが出る予定が、NoStockExceptionが出てしまった.");
		}
	}

	@Test
	public void testInBound_異常系_入荷数オーバー() {
		Stock stock = new Stock(6);
		try {
			stock.incrementQuantity(101);
			fail("入荷数がオーバーしなかった.");
		} catch (StockOverException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testOutBound_異常系_入荷数オーバー_Serviceチェック() {
		try {
			InOutBoundLine inOutBoundLine = new InOutBoundLine("入荷", 1, 81);
			stockService.inBound(inOutBoundLine);
			fail("入荷数がオーバーしなかった.");
		} catch (StockOverException e) {
			assertTrue(true);
		} catch (NoStockException e) {
			fail("StockOverExceptionが出る予定が、NoStockExceptionが出てしまった.");
		}
	}

	@Test
	public void testIsStockDelete_正常系_未削除() throws Exception {
		boolean ans = stockService.isStockDeactive(0);
		if(!ans) {
			assertTrue(true);
		} else {
			fail("未削除ではなかった.");
		}
	}

	@Test
	public void testIsStockDelete_正常系_削除済() throws Exception {
		boolean ans = stockService.isStockDeactive(4);
		if(ans) {
			assertTrue(true);
		} else {
			fail("削除済みではなかった.");
		}
	}

	@Test
	public void testIsStockCreate_正常系() {
		try {
			boolean ans = stockService.isStockCreate(8);
			if(ans) {
				assertTrue(true);
			}
		} catch (GoodsCodeDupulicateException e) {
			fail("在庫が作れなかった.");
		}
	}

	@Test
	public void testIsStockCreate_正常系_登録できない() {
		try {
			boolean ans = stockService.isStockCreate(7);
			if(!ans) {
				assertTrue(true);
			}
		} catch (GoodsCodeDupulicateException e) {
			fail("登録できる状態だった.");
		}
	}

	@Test
	public void testIsStockCreate_異常系_商品コードの重複() {
		try {
			stockService.isStockCreate(0);
			fail("商品コードが重複していなかった.");
		} catch (GoodsCodeDupulicateException e) {
			assertTrue(true);
		}
	}
}