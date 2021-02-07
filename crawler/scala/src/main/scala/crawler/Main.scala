package crawler

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets


object Main extends App {

  val crawler = new StockDiscussionCrawler("005930")
  val script = crawler.getStartDiscussionUrl


  print(script)

}
