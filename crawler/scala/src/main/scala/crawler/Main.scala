package crawler

import crawler.StockDiscussionCrawler
import crawler.manager.file.GoogleFileUploader


object Main {
  def main(args: Array[String]): Unit = {

    val stockItemCode: String = "005930"
    val cycleTime = 10000
    val crawler = new StockDiscussionCrawler(stockItemCode, new GoogleFileUploader(stockItemCode, cycleTime))

    crawler.runBackward
  }

}
