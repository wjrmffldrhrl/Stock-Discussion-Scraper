package crawler

import crawler.manager.file.{GoogleFileUploader, StockDiscussionFileCreator}

import scala.::
import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {

    var itemCodeStr = ""

    try {
      Source.fromFile("stock_list.txt").getLines().filter(line => !line.startsWith("#")).foreach(itemCode => {

        val stockCrawler = new NaverStockDiscussionCrawler(itemCode, 300, new StockDiscussionFileCreator(itemCode))
        stockCrawler.setRunDirection("frontward")

        new Thread(stockCrawler).start()
        itemCodeStr += ("," + itemCode)
      })

      new Thread(
        new GoogleFileUploader(itemCodeStr.substring(1).split(","), 1000 * 60 * 60)
      ).start()


    } catch {
      case ex: Exception => println(ex)
    }





  }

}
