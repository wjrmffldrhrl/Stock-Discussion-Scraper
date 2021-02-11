package crawler

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import java.io.{File, FileWriter, PrintWriter}
import java.text.SimpleDateFormat
import java.time.LocalDateTime


class StockDiscussionCrawler(itemCode: String) {

  val mainUrl: String = "https://finance.naver.com"
  val boardUrl: String = "/item/board.nhn"
  val browser = JsoupBrowser()

  private def getStartDiscussionUrl: String = {
    val targetUrl = this.mainUrl + this.boardUrl + "?code=" + this.itemCode


    val script = getScript(targetUrl)




    val doc = browser.parseString(script)

    doc >> attr("href")("a[onclick=return singleSubmitCheck();]")

  }

  private def getDiscussion(discussionUrl: String): StockDiscussion = {
    val targetUrl = this.mainUrl + discussionUrl
    val script = getScript(targetUrl)

    val doc = browser.parseString(script)

    val title = (doc >> attr("title")("strong[class=c p15]")).replace(",", " ")
    val content = (doc >> text("#body")).replace(",", " ")
    val date = (doc >> text("th[style=padding: 5px 14px 7px 0pt; border-bottom: 1px solid #E5E5E5;]")).replace(" ", "T")
    val discussionUrlTags = doc >> elements("a")

    val discussionUrls = discussionUrlTags.filter(u => u.hasAttr("title")).map(u => u.attr("href")).filter(u => u.startsWith("board_read"))
    val previousDiscussionUrl = discussionUrls.last

    var nextDiscussionUrl = ""

    if (discussionUrls.size > 1) {
      nextDiscussionUrl = discussionUrls.head
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
    while(count < amount) {

      Thread.sleep(5000)
      val discussion = getDiscussion(url)

      if (discussion.hasNextDiscussionUrl) {
        url = "/item/" + discussion.nextDiscussionUrl
        count += 1
        println(discussion)
      }
    }
  }

  def runBackward: Unit = {
    var url = getStartDiscussionUrl

    var fileName = "t"
    var writer = initOutputFileWriter(fileName)

    var run = true
    while(run) {


      Thread.sleep(500)
      val discussion = getDiscussion(url)

      if(discussion.previousDiscussionUrl.length < 1) {
        run = false
      }

      url = "/item/" + discussion.previousDiscussionUrl

      val discussionDate = discussion.date.split("T")(0).replace(".", "_")

      if (!discussionDate.equals(fileName)) {
        writer.close()
        writer = initOutputFileWriter(discussionDate)
      }

      println(discussion.toCsv)
      writer.write(discussion.toCsv + "\n")

    }

    writer.close()

  }

  def initOutputFileWriter(fileName: String): FileWriter = {
    var csv = new File("discussion/" + this.itemCode + "/" + fileName + ".csv")
    val isFileExists = csv.exists

    var writer = new FileWriter(csv, true)

    if(!isFileExists && !csv.isDirectory) {
      writer.write("url,title,content,date,previousDiscussionUrl,nextDiscussionUrl" + "\n")
    }

    writer
  }
}
