package crawler

import crawler.manager.file.FileManager
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import java.io.{BufferedWriter, File, FileNotFoundException, FileReader, FileWriter}
import java.time.LocalDateTime
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
  private val fileSplitThreshold = 10000
  private var runDirection = "backward"

  def setRunDirection(runDirection: String): Unit = {
    this.runDirection = runDirection
  }


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

  def runFrontward(): Unit = {

    var url = getStartDiscussionUrl

    new Thread(fileManager).start()

    var fileName = "init"
    var csvWriter = initOutputFileWriter(fileName)
    var discussionCount = 0

    var dataWaitTime = 1
    while (true) {

      try {
        Thread.sleep(cycleTime * dataWaitTime)

        val discussion = getDiscussion(url)

        if (discussion.previousDiscussionUrl.length < 1) {
          dataWaitTime += 1
        } else {
          println(discussion)
          val discussionDate = discussion.date.split("T")(0).replace(".", "_")
          dataWaitTime = 1
          url = "/item/" + discussion.previousDiscussionUrl

          discussionCount += 1
          if (discussionCount >= fileSplitThreshold || fileName.equals("init")) {
            csvWriter.close()
            csvWriter = initOutputFileWriter(discussionDate)
            fileName = discussionDate
            discussionCount = 0

            Using(new BufferedWriter(new FileWriter("discussion/" + this.itemCode + "/last_url.log"))) {
              writer => writer.write(url + "\r\n")
            }
          }


          csvWriter.write(discussion.toCsv + "\n")

        }

      } catch {
        case e: Exception => {
          println(LocalDateTime.now() + "[Error in  " + this.itemCode + " : " + url + "] " + e)
          new File("discussion/" + this.itemCode + "/last_url.log").delete()
          url = getStartDiscussionUrl
        }
      }

    }

    csvWriter.close()
  }

  def runBackward(): Unit = {
    var url = getStartDiscussionUrl

    new Thread(fileManager).start()

    var fileName = "init"
    var csvWriter = initOutputFileWriter(fileName)
    var discussionCount = 0
    while (url.length > 1) {

      try {
        Thread.sleep(cycleTime)

        val discussion = getDiscussion(url)
        val discussionDate = discussion.date.split("T")(0).replace(".", "_")


        if (discussion.nextDiscussionUrl.length < 1) {
          url = ""
        } else {
          url = "/item/" + discussion.nextDiscussionUrl
        }

        discussionCount += 1
        if (discussionCount >= fileSplitThreshold || fileName.equals("init")) {
          csvWriter.close()

          csvWriter = initOutputFileWriter(discussionDate)
          fileName = discussionDate
          discussionCount = 0

          Using(new BufferedWriter(new FileWriter("discussion/" + this.itemCode + "/last_url.log"))) {
            writer => writer.write(url + "\r\n")
          }
        }

        csvWriter.write(discussion.toCsv + "\n")

      } catch {
        case e: Exception => println(LocalDateTime.now() + "[Error in  " + this.itemCode + " : " + url + "] " + e)
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
    println(s"run $runDirection with " + itemCode)

    if (this.runDirection.equals("backward")) {
      runBackward()
    } else if (this.runDirection.equals("frontward")) {
      runFrontward()
    } else {
      throw new RuntimeException(s"Run direction exception with [$runDirection]")
    }

  }
}
