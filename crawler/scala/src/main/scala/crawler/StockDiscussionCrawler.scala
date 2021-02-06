package crawler

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}

class StockDiscussionCrawler(code: String) {

  val browser: Browser = JsoupBrowser()
  val itemCode: String = code
  val mainUrl: String = "https://finance.naver.com"
  val boardUrl: String = "/item/board.nhn"

  def getStartDiscussionUrl(): String = {
    val targetUrl = this.mainUrl + this.boardUrl + "?code=" + this.itemCode

    this.browser.userAgent()
    val doc = this.browser.get(targetUrl)
    doc.toString

  }


}
