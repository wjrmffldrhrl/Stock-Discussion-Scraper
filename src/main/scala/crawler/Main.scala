package crawler

import crawler.manager.file.GoogleFileUploader
import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {


    try {
      Source.fromFile("stock_list.txt").getLines().filter(line => !line.startsWith("#")).foreach(itemCode => {

        val stockCrawler = new NaverStockDiscussionCrawler(itemCode, 300, new GoogleFileUploader(itemCode, 10000))
        stockCrawler.setRunDirection("frontward")

        new Thread(stockCrawler)
          .start()
      })
    } catch {
      case ex: Exception => println(ex)
    }





  }

}
