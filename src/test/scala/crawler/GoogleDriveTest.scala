package crawler

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.drive.Drive
import crawler.manager.file.GoogleDriveManager
import org.scalatest.FunSuite

class GoogleDriveTest extends FunSuite{

  test("test google API") {
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()

    val service = new Drive.Builder(HTTP_TRANSPORT, GoogleDriveManager.getJsonFactory,
      GoogleDriveManager.getCredentials(HTTP_TRANSPORT)).setApplicationName("OAuth client").build()

    service.files().list().setPageSize(100).setFields("nextPageToken, files(id, name)").execute().getFiles
      .forEach(file => println(file.getName, file.getId))

  }


  test("test file upload ") {
    println(GoogleDriveManager.uploadFile(new java.io.File("discussion/005930/2021_02_13.csv")))

  }

  test("test create directory") {
    println(GoogleDriveManager.createDirectory("005930"))
  }

  test("test find File") {
    println(GoogleDriveManager.findDirectory("005930"))
  }

}
