package crawler

class StockDiscussionCrawler(code: String) {
  val itemCode: String = code
  val mainUrl: String = "https://finance.naver.com"
  val boardUrl: String = "/item/board.nhn"

  def getStartDiscussionUrl: String = {
    val targetUrl = this.mainUrl + this.boardUrl + "?code=" + this.itemCode

    val headers = Map[String, String]("referer" -> targetUrl)

    val request = requests.get(targetUrl, headers = headers)


    val script = new String(request.bytes, "EUC-KR")

    script
  }

}
