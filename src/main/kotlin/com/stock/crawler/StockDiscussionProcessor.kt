package com.stock.crawler

import com.stock.StockDiscussion

interface StockDiscussionProcessor {

    fun processing(discussion: StockDiscussion)
}
