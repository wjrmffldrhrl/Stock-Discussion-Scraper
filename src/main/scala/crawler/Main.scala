package crawler

import crawler.manager.file.GoogleFileUploader
import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {


    try {
      Source.fromFile("stock_list.txt").getLines().filter(line => !line.startsWith("#")).foreach(itemCode => {
        new Thread(new NaverStockDiscussionCrawler(itemCode, 100, new GoogleFileUploader(itemCode, 10000)))
          .start()
      })
    } catch {
      case ex: Exception => println(ex)
    }





  }

}
