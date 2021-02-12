package crawler.manager.file

trait FileManager extends Thread {

  val cycleTime: Int

  def work()

  override def run(): Unit = {
    Thread.sleep(cycleTime)
    work()
  }
}
