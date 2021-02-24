package crawler

trait StockDiscussionCrawler extends Runnable{

  def work()

  override def run(): Unit = {
    println("run crawler")
    work()
    println("end crawler")
  }


}
