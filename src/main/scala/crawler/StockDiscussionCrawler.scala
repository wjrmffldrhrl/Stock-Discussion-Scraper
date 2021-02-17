package crawler

trait StockDiscussionCrawler extends Runnable{

  def work: Unit

  override def run(): Unit = {
    println("run crawler")
    work

  }


}
