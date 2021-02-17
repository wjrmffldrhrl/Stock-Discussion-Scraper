package crawler.manager.file

import java.io.File
import java.time.LocalDateTime

/**
 * 파일 처리 클래스
 *
 * @param itemCode 해당 파일 처리 클래스가 사용되는 종목 코드
 * @param cycleTime 해당 파일 처리 클래스가 동작하는 주기
 */
class GoogleFileUploader(itemCode: String, inputCycleTime: Int) extends FileManager {


  private val targetDirectoryPath = "discussion/" + itemCode
  private val targetDirectory = new File(targetDirectoryPath)


  private var oldFileName = "init.csv"
  override val cycleTime: Int = inputCycleTime

  private def uploadFile(targetFileName: String): Unit ={

    val targetFile = new File(targetDirectoryPath + "/" + oldFileName)

    if (!targetFileName.startsWith("init")) {
      println("[" + LocalDateTime.now() + "] " + this.itemCode + " upload file : " + targetFileName)
      GoogleDriveManager.uploadFile(targetFile)
    }

    targetFile.delete()


  }

  override def work(): Unit = {

    val otherFiles = targetDirectory.list().filter(file => file.endsWith(".csv")).filter(file => !file.equals(oldFileName))

    if (!otherFiles.isEmpty) {
      val latestFileName = otherFiles.head

      uploadFile(oldFileName)


      oldFileName = latestFileName
    }
  }


}
