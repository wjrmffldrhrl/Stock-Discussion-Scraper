package crawler

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import crawler.manager.file.GoogleDriveManager
import org.scalatest.FunSuite

import java.io.File
import com.google.api.services.drive.model.File

import java.util

class GoogleDriveTest extends FunSuite{

  test("test google API") {
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()

    val service = new Drive.Builder(HTTP_TRANSPORT, GoogleDriveManager.getJsonFactory,
      GoogleDriveManager.getCredentials(HTTP_TRANSPORT)).setApplicationName("OAuth client").build()

    service.files().list().setPageSize(100).setFields("nextPageToken, files(id, name)").execute().getFiles
      .forEach(file => println(file.getName, file.getId))

  }


  test("test file upload ") {
    val fileMetadata = new com.google.api.services.drive.model.File()

    val directoryId = "1NvZhfU-8D7SQurvt0CZtP2TA-Er7JADs"


    fileMetadata.setName("2021_02_10.csv")
    fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet")

    val testFile = new java.io.File("test/005930/2021_02_10.csv")
    val mediaContent = new FileContent("text/csv", testFile)

    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val service = new Drive.Builder(HTTP_TRANSPORT, GoogleDriveManager.getJsonFactory,
      GoogleDriveManager.getCredentials(HTTP_TRANSPORT)).setApplicationName("stock-discussion-analysis").build()

    val result = service.files().create(fileMetadata, mediaContent)
      .setFields("id")
      .execute()

    println(result.getId)


  }

}
