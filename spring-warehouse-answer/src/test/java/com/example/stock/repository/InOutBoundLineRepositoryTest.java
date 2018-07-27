package com.example.stock.repository;

import static org.junit.Assert.assertEquals;
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

import com.example.stock.domain.InOutBoundLine;
import com.example.stock.service.InOutBoundLineRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/data/stock/INPUT_INOUTBOUND_DATA.xml")
@Transactional
public class InOutBoundLineRepositoryTest {

	@Autowired
	private InOutBoundLineRepository inOutBoundLineRepository;

	@Test
	public void testFindInOutBoundHistory_正常系() throws Exception {
		List<InOutBoundLine> inOutBoundLineList = inOutBoundLineRepository.findInOutBoundHistory();

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
		List<InOutBoundLine> inOutBoundLineList = inOutBoundLineRepository.findInOutBoundHistory();
		assertEquals(0, inOutBoundLineList.size());
	}

	@Test
	@ExpectedDatabase(
			value = "/data/stock/EXPECTED_CREATE_INOUTBOUND_DATA.xml",
			assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testCreateInOutBoundHistory_正常系() throws Exception {
		InOutBoundLine inOutBoundLine = new InOutBoundLine("出荷", 2, 6);
		inOutBoundLineRepository.createInOutBoundHistory(inOutBoundLine);
	}

}