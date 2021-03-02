package com.stock

class StockDiscussion(
    val url: String,
    val title: String,
    val content: String,
    val date: String,
    val previousDiscussionUrl: String,
    val nextDiscussionUrl: String
) {

    fun toCsv(): String {
        val csvTitle = title.replace(",", " ")
        val csvContent = content.replace(",", " ")
        val csvUrl = url.replace(",", " ")
        val csvPreviousDiscussionUrl = previousDiscussionUrl.replace(",", " ")
        val csvNextDiscussionUrl = nextDiscussionUrl.replace(",", " ")


        return "$date,$csvTitle,$csvContent,$csvUrl,$csvPreviousDiscussionUrl,$csvNextDiscussionUrl"
    }
    override fun toString() = "url : [$url] \n title : [$title] \n content: [$content] \n date: [$date] \n previousDiscussionUrl : [$previousDiscussionUrl] \n nextDiscussionUrl : [$nextDiscussionUrl]"
}