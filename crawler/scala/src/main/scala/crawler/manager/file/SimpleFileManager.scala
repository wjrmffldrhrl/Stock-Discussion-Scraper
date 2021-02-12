package crawler.manager.file

class SimpleFileManager(initCycleTime: Int) extends FileManager {
  override val cycleTime: Int = initCycleTime

  override def work(): Unit = {
    println("good!")
  }
}
