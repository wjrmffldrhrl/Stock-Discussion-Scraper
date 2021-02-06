package crawler
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

//https://github.com/ruippeixotog/scala-scraper
object Main extends App {
  val samsungStockDiscussionCrawler = new StockDiscussionCrawler("005930")

  val result = samsungStockDiscussionCrawler.getStartDiscussionUrl()

  print(result)

}
