package com.stock

class StockDiscussion(
    val url: String,
    val title: String,
    val content: String,
    val date: String,
    val previousDiscussionUrl: String,
    val nextDiscussionUrl: String
) {

    fun toCsv() = "$date,$title,$content,$url,$previousDiscussionUrl,$nextDiscussionUrl"
    override fun toString() = "url : [$url] \n title : [$title] \n content: [$content] \n date: [$date] \n previousDiscussionUrl : [$previousDiscussionUrl] \n nextDiscussionUrl : [$nextDiscussionUrl]"
}