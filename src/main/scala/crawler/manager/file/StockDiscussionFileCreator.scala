package crawler.manager.file
import crawler.StockDiscussion

import java.io.{BufferedWriter, File, FileWriter}

class StockDiscussionFileCreator(itemCode: String) extends StockDiscussionProcessor {

  val directoryPath = "discussion/" + this.itemCode
  val directory = new File(directoryPath)
  if (!directory.exists()) {
    directory.mkdirs()
  }

  var processingCount = 0
  var fileName = "init"
  val fileExtension = ".csv"
  var csvWriter = initOutputFileWriter(fileName)




  private def initOutputFileWriter(fileName: String): BufferedWriter = {

    val csv = new File(directoryPath + "/" + fileName + fileExtension)
    val writer = new BufferedWriter(new FileWriter(csv, true))

    writer.write("date,title,content,url,previousDiscussionUrl,nextDiscussionUrl" + "\n")

    writer
  }

  override def discussionProcessing(discussion: StockDiscussion): Unit = {
    val discussionDate = discussion.date.split("T")(0).replace(".", "_")

    if (!discussionDate.equals(fileName) || fileName.equals("init")) {
      csvWriter.close()
      csvWriter = initOutputFileWriter(discussionDate)
      fileName = discussionDate

    }

    csvWriter.write(discussion.toCsv + "\n")
    csvWriter.flush()

  }
}
