package com.stock.crawler

import com.stock.KoreanRequests
import com.stock.StockDiscussion
import org.jsoup.Jsoup
import java.io.*
import java.time.LocalDateTime

class NaverStockDiscussionCrawler(private val itemCode: String, override val cycleTime: Int, private val stockDiscussionProcessor:StockDiscussionProcessor = StockDiscussionPrinter()) : StockDiscussionCrawler {

    private val mainUrl: String = "https://finance.naver.com"
    private val boardUrl: String = "/item/board.nhn"
    private var runDirection = "frontward"

    private fun runFrontward() {
        var url = getStartDiscussionUrl()
        var dataWaitTime = 1
        while (url.isNotEmpty()) {
            try {
                Thread.sleep((cycleTime * dataWaitTime).toLong())

                val discussion = getDiscussion(url)

                if (discussion.previousDiscussionUrl.length < 1) {
                    dataWaitTime += 1
                } else {
                    url = "/item/" + discussion.previousDiscussionUrl
                    stockDiscussionProcessor.processing(discussion)

                    BufferedWriter(FileWriter("discussion/" + this.itemCode + "/last_url.log")).use { writer ->
                        writer.write(url)
                    }

                }

            } catch(e : Exception) {

                    println(LocalDateTime.now().toString() + "[Error in  " + itemCode + " : " + url + "] ")
                    File ("discussion/" + this.itemCode + "/last_url.log").delete()
                    url = getStartDiscussionUrl()

            }
        }
    }

    fun getDiscussion(discussionUrl: String): StockDiscussion {
        val targetUrl = mainUrl + discussionUrl

        val script = KoreanRequests.getScript(targetUrl)

        val doc = Jsoup.parse(script)

        val title = doc.select("strong[class=c p15]").attr("title")
        val content = doc.select("#body").text()
        val date = doc.select("th[style=padding: 5px 14px 7px 0pt; border-bottom: 1px solid #E5E5E5;]").text().replace(" ", "T")
        val discussionUrlTags = doc.getElementsByTag("a")
        val nextPrevious = doc.select("span[class=p11 gray03]")
        val discussionUrls = discussionUrlTags.filter { url -> url.hasAttr("title") }
            .map { url -> url.attr("href") }
            .filter { url -> url.startsWith("board_read") }


        var previousDiscussionUrl = ""
        var nextDiscussionUrl = ""

        if (nextPrevious.size == 2) {
            previousDiscussionUrl = discussionUrls.first()
            nextDiscussionUrl = discussionUrls.last()
        } else {
            if (nextPrevious.first().text().equals("이전")) {
                previousDiscussionUrl = discussionUrls.first()
            } else {
                nextDiscussionUrl = discussionUrls.last()
            }
        }

        return StockDiscussion(targetUrl, title, content, date, previousDiscussionUrl, nextDiscussionUrl)

    }

    private fun runBackward() {
        TODO("Not yet implemented")
    }

    fun getStartDiscussionUrl(): String {

        try {
            BufferedReader(FileReader("discussion/$itemCode/last_url.log")).use {
                val lastUrl: String = it.readLine()
                if (lastUrl.length < 5) {
                    return lastUrl
                }
            }
        } catch (e: FileNotFoundException) {
            println("Last url file not found")
        } catch (e: NullPointerException) {
            println("Last url file is empty")
        }

        val script = KoreanRequests.getScript("$mainUrl$boardUrl?code=$itemCode")

        return Jsoup.parse(script)
            .getElementsByAttributeValue("onclick", "return singleSubmitCheck();").first().attr("href")

    }

    override fun work() {
        println("run $runDirection with $itemCode")

        try {

            when (this.runDirection) {
                "backward" -> {
                    runBackward()
                }
                "frontward" -> {
                    runFrontward()
                }
                else -> {
                    throw RuntimeException("Run direction exception with [$runDirection]")
                }
            }
        } catch (e: RuntimeException) {
            println("Error in crawler $itemCode : $e")
        }
    }
}