package com.stock.crawler

import com.stock.utils.StockDiscussion

interface StockDiscussionProcessor {

    fun processing(discussion: StockDiscussion)
}
