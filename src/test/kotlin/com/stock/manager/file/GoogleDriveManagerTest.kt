package com.stock.manager.file

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.drive.Drive
import org.junit.jupiter.api.Test
import java.io.File

class GoogleDriveManagerTest {

    @Test
    fun testConnect() {
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()

        val service = Drive.Builder(HTTP_TRANSPORT, GoogleDriveManager.jsonFactory,
        GoogleDriveManager.getCredentials(HTTP_TRANSPORT)).setApplicationName("OAuth client").build()

        service.files().list().setPageSize(100).setFields("nextPageToken, files(id, name)").execute().files
            .forEach{file -> println(file.name + " " + file.id)}

    }
}