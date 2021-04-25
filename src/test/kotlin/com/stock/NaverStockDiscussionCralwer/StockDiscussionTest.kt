package com.stock.NaverStockDiscussionCralwer

import com.stock.crawler.NaverStockDiscussionCrawler
import com.stock.crawler.StockDiscussionFileCreator
import com.stock.utils.StockDiscussion
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StockDiscussionTest {

    @Test
    fun testToCsv() {
        Assertions.assertEquals(
            "2021.02.26T03:58,https://finance.naver.com/item/board_read.nhn?code=005935&nid=165469125&st=&sw=&page=1,검은 금요일~~ㅋㅋㅋㅋ,우짜냐?ㅋㅋㅋ",
            StockDiscussion(
                "https://finance.naver.com/item/board_read.nhn?code=005935&nid=165469125&st=&sw=&page=1",
                "검은 금요일~~ㅋㅋㅋㅋ", "우짜냐?ㅋㅋㅋ", "2021.02.26T03:58",
                "board_read.nhn?code=005935&nid=165470265&st=&sw=&page=1",
                "board_read.nhn?code=005935&nid=165468653&st=&sw=&page=1"
            ).toCsv()
        )
    }

    @Test
    fun test() {
    }

}