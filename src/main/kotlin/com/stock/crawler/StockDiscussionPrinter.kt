package com.stock.crawler

import com.stock.utils.StockDiscussion

/**
 * Just print stock discussion
 *
 *
 * Base argument in stock discussion crawler
 */
internal class StockDiscussionPrinter: StockDiscussionProcessor {
    override fun processing(discussion: StockDiscussion) {
        println(discussion)
    }
}