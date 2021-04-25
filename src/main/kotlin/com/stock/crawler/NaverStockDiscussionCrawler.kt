package com.stock.crawler

import com.stock.utils.KoreanRequests
import com.stock.utils.StockDiscussion
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.net.UnknownHostException
import java.time.LocalDateTime

class NaverStockDiscussionCrawler(
    private val itemCode: String, override val cycleTime: Int,
    private val stockDiscussionProcessor: StockDiscussionProcessor = StockDiscussionPrinter()
) : StockDiscussionCrawler {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)
    private val mainUrl: String = "https://finance.naver.com"
    private val boardUrl: String = "/item/board.nhn"
    private var runDirection = "frontward"

    override fun work() {
        logger.info("[${LocalDateTime.now()}] run $runDirection with $itemCode")

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
            logger.error("[${LocalDateTime.now()}] Error in crawler $itemCode : $e")
        }
    }

    private fun runBackward() {
        TODO("Not yet implemented")
    }

    private fun runFrontward() {
        var url = getStartDiscussionUrl()
        var dataWaitTime = 1
        logger.info("[${LocalDateTime.now()}] Start Naver Stock Discussion Crawling")

        while (url.isNotEmpty()) {
            try {
                Thread.sleep((cycleTime * dataWaitTime).toLong())

                val discussion = getDiscussion(url)

                // 최신 discussion이 없을 때 대기 시간을 늘린다.
                if (discussion.previousDiscussionUrl.isEmpty()) {

                    if (dataWaitTime < 4096) {
                        dataWaitTime *= 2
                    }

                    logger.info("[${LocalDateTime.now()}] No data now. Increase wait time to $dataWaitTime")
                    continue
                }

                url = "/item/" + discussion.previousDiscussionUrl
                stockDiscussionProcessor.processing(discussion)

//                println(discussion.toCsv())
                BufferedWriter(FileWriter("discussion/" + this.itemCode + "/last_url.log")).use { writer ->
                    writer.write(url)
                }

                // 데이터가 있을 경우 대기시간 초기화
                dataWaitTime = 1


            } catch (e: Exception) {

                logger.error("[${LocalDateTime.now()}] Error in  $itemCode : $url")
                File("discussion/" + this.itemCode + "/last_url.log").delete()
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
        val date = doc.select("th[style=padding: 5px 14px 7px 0pt; border-bottom: 1px solid #E5E5E5;]").text()
            .replace(" ", "T")
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

    fun getStartDiscussionUrl(): String {
        while (true) {
            try {
                BufferedReader(FileReader("discussion/$itemCode/last_url.log")).use {
                    val lastUrl: String = it.readLine()
                    if (lastUrl.length < 5) {
                        return lastUrl
                    }
                }
            } catch (e: FileNotFoundException) {
                logger.debug("[${LocalDateTime.now()}] Last url file not found")
            } catch (e: NullPointerException) {
                logger.debug("[${LocalDateTime.now()}] Last url file is empty")
            }

            try {
                val script = KoreanRequests.getScript("$mainUrl$boardUrl?code=$itemCode")

                return Jsoup.parse(script)
                    .getElementsByAttributeValue("onclick", "return singleSubmitCheck();").first().attr("href")

            } catch (e: UnknownHostException) {
                logger.error("[${LocalDateTime.now()}] Error in this url : $mainUrl$boardUrl?code=$itemCode \n ${e.printStackTrace()}")

            }

            Thread.sleep(cycleTime.toLong())
        }

    }
}