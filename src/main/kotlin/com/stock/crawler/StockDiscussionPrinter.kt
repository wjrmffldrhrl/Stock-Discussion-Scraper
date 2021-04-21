package com.stock.crawler

import com.stock.utils.StockDiscussion

class StockDiscussionPrinter: StockDiscussionProcessor {
    override fun processing(discussion: StockDiscussion) {
        println(discussion)
    }
}