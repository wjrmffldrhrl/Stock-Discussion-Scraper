package crawler

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import java.io.{BufferedReader, File, FileReader, FileWriter, PrintWriter}
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import scala.util.Using
import scala.util.control.Exception


class StockDiscussionCrawler(itemCode: String) {

  val mainUrl: String = "https://finance.naver.com"
  val boardUrl: String = "/item/board.nhn"
  val browser = JsoupBrowser()

  def getStartDiscussionUrl: String = {
    val targetUrl = this.mainUrl + this.boardUrl + "?code=" + this.itemCode


    val script = getScript(targetUrl)


    val doc = browser.parseString(script)

    doc >> attr("href")("a[onclick=return singleSubmitCheck();]")

  }

  def getDiscussion(discussionUrl: String): StockDiscussion = {
    val targetUrl = this.mainUrl + discussionUrl
    val script = getScript(targetUrl)

    val doc = browser.parseString(script)

    val title = (doc >> attr("title")("strong[class=c p15]")).replace(",", " ")
    val content = (doc >> text("#body")).replace(",", " ")
    val date = (doc >> text("th[style=padding: 5px 14px 7px 0pt; border-bottom: 1px solid #E5E5E5;]")).replace(" ", "T")
    val discussionUrlTags = doc >> elements("a")
    val nextPrevious = doc >> texts("span[class=p11 gray03]")
    val discussionUrls = discussionUrlTags.filter(u => u.hasAttr("title")).map(u => u.attr("href")).filter(u => u.startsWith("board_read"))

    var previousDiscussionUrl = ""
    var nextDiscussionUrl = ""

    if (nextPrevious.size == 2) {
      previousDiscussionUrl = discussionUrls.head
      nextDiscussionUrl = discussionUrls.last
    } else {
      if (nextPrevious.head.equals("이전")) {
        previousDiscussionUrl = discussionUrls.head
      } else {
        nextDiscussionUrl = discussionUrls.last
      }
    }

    new StockDiscussion(targetUrl, title, content, date, previousDiscussionUrl, nextDiscussionUrl)

  }

  private def getScript(url: String): String = {
    val headers = Map[String, String]("referer" -> url)

    val request = requests.get(url, headers = headers)

    val script = new String(request.bytes, "EUC-KR")

    script
  }

  def runFrontward(amount: Int): Unit = {
    //    val url = "/item/board_read.nhn?code=005930&nid=162767151&st=&sw=&page=1"
    var url = getStartDiscussionUrl

    var count = 0
    while (count < amount) {

      Thread.sleep(5000)
      val discussion = getDiscussion(url)

      if (discussion.previousDiscussionUrl.length > 1) {
        url = "/item/" + discussion.previousDiscussionUrl
        count += 1
        println(discussion)
      }
    }
  }

  def runBackward: Unit = {
    var url = getStartDiscussionUrl
    new FileChecker(this.itemCode, 10000).start()
    var fileName = "init"
    var writer = initOutputFileWriter(fileName)

    while (url.length > 1) {

      try {
        Thread.sleep(100)

        val discussion = getDiscussion(url)


        val discussionDate = discussion.date.split("T")(0).replace(".", "_")

        if (!discussionDate.equals(fileName)) {
          writer.close()
          writer = initOutputFileWriter(discussionDate)
          fileName = discussionDate
        }

        println(discussion.toCsv)
        writer.write(discussion.toCsv + "\n")

        if (discussion.nextDiscussionUrl.length < 1) {
          url = ""
        } else {
          url = "/item/" + discussion.nextDiscussionUrl
        }
      } catch {
        case e: Exception => println("[Error in : " + url + "] " + e)
      }

    }

    writer.close()

  }

  private def initOutputFileWriter(fileName: String): FileWriter = {

    val directoryPath = "discussion/" + this.itemCode

    val directory = new File(directoryPath)

    if (!directory.exists()) {
      directory.mkdirs()
    }

    val csv = new File(directoryPath + "/" + fileName + ".csv")

    val writer = new FileWriter(csv, true)

    if (!csv.exists) {
      writer.write("date,title,content,url,previousDiscussionUrl,nextDiscussionUrl" + "\n")
    }

    writer
  }
}
