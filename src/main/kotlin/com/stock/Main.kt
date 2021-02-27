package com.stock

import com.google.common.collect.Lists
import com.stock.crawler.NaverStockDiscussionCrawler
import com.stock.crawler.StockDiscussionFileCreator
import com.stock.manager.file.GoogleFileUploader
import java.io.BufferedReader
import java.io.FileReader


fun main() {

    val itemCodes = ArrayList<String>()

    BufferedReader(FileReader("stock_list.txt")).useLines {
        it.filter { line -> !line.startsWith("#") }.forEach { useItemCode ->  itemCodes.add(useItemCode)}
    }

    itemCodes.forEach { itemCode ->
        Thread(NaverStockDiscussionCrawler(itemCode, 1000, StockDiscussionFileCreator("005930"))).start()
    }



    Thread(GoogleFileUploader(itemCodes.toTypedArray(), 10000))
}
