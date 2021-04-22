package com.stock.utils

class StockDiscussion(
    val url: String,
    val title: String,
    val content: String,
    val date: String,
    val previousDiscussionUrl: String,
    val nextDiscussionUrl: String
) {

    fun toCsv(): String {
        val title = this.title.replace(",", " ")
        val content = this.content.replace(",", " ")

        return "$date,$url,$title,$content"
    }
    override fun toString() = "url : [$url] \n title : [$title] \n content: [$content] \n date: [$date] \n previousDiscussionUrl : [$previousDiscussionUrl] \n nextDiscussionUrl : [$nextDiscussionUrl]"
}