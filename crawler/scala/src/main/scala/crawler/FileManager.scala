package crawler

import java.io.File

/**
 * 파일 처리 클래스
 *
 * @param itemCode 해당 파일 처리 클래스가 사용되는 종목 코드
 * @param cycleTime 해당 파일 처리 클래스가 동작하는 주기
 */
class FileManager(itemCode: String, cycleTime: Int) extends Thread{
  private val targetDirectoryPath = "discussion/" + itemCode
  private val targetDirectory = new File(targetDirectoryPath)

  private var oldFileName = "init.csv"

  private def uploadFile(targetFile: String): Unit ={
    println("upload file : " + targetFile)

    new File(targetDirectoryPath + "/" + oldFileName).delete()


  }


  override def run(): Unit = {
    while(true) {
      Thread.sleep(cycleTime)

      val otherFiles = targetDirectory.list().filter(file => !file.equals(oldFileName))

      if (!otherFiles.isEmpty) {
        val latestFileName = otherFiles.head

        uploadFile(oldFileName)


        oldFileName = latestFileName
      }



    }

  }


}
