package com.stock

import com.stock.crawler.NaverStockDiscussionCrawler
import com.stock.crawler.StockDiscussionFileCreator
import java.io.BufferedReader
import java.io.FileReader

fun main() {

    val itemCodes = ArrayList<String>()

    BufferedReader(FileReader("stock_list.txt")).useLines {
        it.filter { line -> !line.startsWith("#") }
            .filter { line -> line.isNotEmpty() }
            .forEach { useItemCode -> itemCodes.add(useItemCode) }
    }

    itemCodes.forEach { itemCode ->
        Thread(
            NaverStockDiscussionCrawler(itemCode, 1000, StockDiscussionFileCreator(itemCode)), "Crawler $itemCode"
        ).start()
    }

}
