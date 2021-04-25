package com.stock.crawler

import com.stock.utils.StockDiscussion

/**
 * Processing with stock discussion crawler
 */
interface StockDiscussionProcessor {

    fun processing(discussion: StockDiscussion)
}
