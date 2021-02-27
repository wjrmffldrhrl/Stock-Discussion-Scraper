package com.stock.crawler

import com.stock.StockDiscussion

class StockDiscussionPrinter: StockDiscussionProcessor {
    override fun processing(discussion: StockDiscussion) {
        println(discussion)
    }
}