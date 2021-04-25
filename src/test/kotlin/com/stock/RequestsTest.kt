package com.stock

import com.stock.utils.KoreanRequests
import org.junit.jupiter.api.Test

class RequestsTest {

    @Test
    fun testGetScript() {
        println(KoreanRequests.getScript("https://finance.naver.com/item/board_read.nhn?code=005930&nid=165685518&st=&sw=&page=1"))
    }
}