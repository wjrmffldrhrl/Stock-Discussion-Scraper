package crawler

import java.io.File

class FileChecker(itemCode: String, cycleTime: Int) extends Thread{
  private val targetDirectoryPath = "discussion/" + itemCode
  private val targetDirectory = new File(targetDirectoryPath)

  private var oldFileName = "init.csv"

  private def uploadFile(targetFile: String): Unit ={
    println("upload file : " + targetFile)
  }


  override def run(): Unit = {
    while(true) {
      Thread.sleep(cycleTime)

      val otherFiles = targetDirectory.list().filter(file => !file.equals(oldFileName))

      if (!otherFiles.isEmpty) {
        val latestFileName = otherFiles.head

        uploadFile(oldFileName)

        new File(targetDirectoryPath + "/" + oldFileName).delete()

        oldFileName = latestFileName

      }



    }

  }


}
