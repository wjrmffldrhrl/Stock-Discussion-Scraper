package crawler

import crawler.manager.file.FileManager
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import java.io.{BufferedWriter, File, FileNotFoundException, FileReader, FileWriter}
import scala.io.Source
import scala.util.Using


/**
 * 종목 토론방 크롤러
 *
 * @param itemCode 타겟 종목 코드
 */
class NaverStockDiscussionCrawler(itemCode: String, cycleTime: Int, fileManager: FileManager) extends StockDiscussionCrawler {

  private val mainUrl: String = "https://finance.naver.com"
  private val boardUrl: String = "/item/board.nhn"
  private val browser = JsoupBrowser()


  def getStartDiscussionUrl: String = {


    try {

      return Source.fromFile("discussion/" + this.itemCode + "/last_url.log").getLines().next()

    } catch {
      case e: FileNotFoundException => println("last url file not found")
      case e: NoSuchElementException => println("last url file is empty")
    }

    browser.parseString(
      getScript(
        this.mainUrl + this.boardUrl + "?code=" + this.itemCode
      )
    ) >> attr("href")("a[onclick=return singleSubmitCheck();]")
  }

  def getDiscussion(discussionUrl: String): StockDiscussion = {
    val targetUrl = this.mainUrl + discussionUrl

    val doc = browser.parseString(
      getScript(targetUrl)
    )

    val title = (doc >> attr("title")("strong[class=c p15]")).replace(",", " ")
    val content = (doc >> text("#body")).replace(",", " ")
    val date = (doc >> text("th[style=padding: 5px 14px 7px 0pt; border-bottom: 1px solid #E5E5E5;]")).replace(" ", "T")
    val discussionUrlTags = doc >> elements("a")
    val nextPrevious = doc >> texts("span[class=p11 gray03]")
    val discussionUrls = discussionUrlTags.filter(u => u.hasAttr("title"))
      .map(u => u.attr("href"))
      .filter(u => u.startsWith("board_read"))

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
    val request = requests.get(url,
      headers = Map[String, String]("referer" -> url)
    )

    new String(request.bytes, "EUC-KR")
  }

  def runFrontward(amount: Int): Unit = {

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

    new Thread(fileManager).start()

    var fileName = "init"
    var csvWriter = initOutputFileWriter(fileName)

    while (url.length > 1) {

      try {
        Thread.sleep(cycleTime)

        val discussion = getDiscussion(url)
        val discussionDate = discussion.date.split("T")(0).replace(".", "_")

        if (!discussionDate.equals(fileName)) {
          csvWriter.close()
          csvWriter = initOutputFileWriter(discussionDate)
          fileName = discussionDate
        }

        csvWriter.write(discussion.toCsv + "\n")

        if (discussion.nextDiscussionUrl.length < 1) {
          url = ""
        } else {
          url = "/item/" + discussion.nextDiscussionUrl
          Using(new BufferedWriter(new FileWriter("discussion/" + this.itemCode + "/last_url.log"))) {
            writer => writer.write(url + "\r\n")
          }

        }

      } catch {
        case e: Exception => println("[Error in : " + url + "] " + e)
      }

    }

    csvWriter.close()

  }

  private def initOutputFileWriter(fileName: String): FileWriter = {

    val directoryPath = "discussion/" + this.itemCode

    val directory = new File(directoryPath)

    if (!directory.exists()) {
      directory.mkdirs()
    }

    val csv = new File(directoryPath + "/" + fileName + ".csv")

    val writer = new FileWriter(csv, true)
    writer.write("date,title,content,url,previousDiscussionUrl,nextDiscussionUrl" + "\n")


    writer
  }


  override def work: Unit = {
    println("run backward with " + itemCode)
    runBackward
  }
}
