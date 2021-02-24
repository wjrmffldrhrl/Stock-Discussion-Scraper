package crawler.manager.file

trait Worker extends Runnable {
  val cycleTime: Int

  def work()

  override def run(): Unit = {
    while (true) {
      Thread.sleep(cycleTime)
      work()
    }
  }
}
