package com.stock.manager.file

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import java.util.Arrays
import com.google.api.services.drive.DriveScopes
import kotlin.Throws
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import java.security.GeneralSecurityException
import com.google.api.services.drive.Drive
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import java.lang.RuntimeException
import com.google.api.client.http.FileContent
import com.google.api.services.drive.model.File
import java.io.*
import java.lang.StringBuilder

object GoogleDriveManager {
    private const val TOKENS_DIRECTORY_PATH = "tokens"
    val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
    private const val discussionDirectoryId = "1NvZhfU-8D7SQurvt0CZtP2TA-Er7JADs"

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES = Arrays.asList(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA)

    //    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    private const val CREDENTIALS_FILE_PATH = "credentials.json"

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
    fun getCredentials(HTTP_TRANSPORT: NetHttpTransport?): Credential {
        // Load client secrets.
        val `in` = FileInputStream(CREDENTIALS_FILE_PATH)
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, jsonFactory, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    fun findDirectory(fileName: String): String {
        val resultStr = StringBuilder()
        val service = getService()
        val files = service.files().list()
            .setQ("mimeType='application/vnd.google-apps.folder' and trashed = false and name='$fileName'")
            .setSpaces("drive")
            .setFields("nextPageToken, files(id, name)")
            .execute()
        for (file in files.files) {
            resultStr.append(",").append(file.id)
        }
        return if (resultStr.length == 0) {
            ""
        } else resultStr.substring(1)
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    fun createDirectory(directoryName: String?): String {
        val fileMetadata = File()
        fileMetadata.name = directoryName
        fileMetadata.mimeType = "application/vnd.google-apps.folder"
        fileMetadata.parents = listOf(discussionDirectoryId)
        val service = getService()
        val result = service.files().create(fileMetadata)
            .setFields("id")
            .execute()
        return result.id
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    private fun getService(): Drive {
            val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
            return Drive.Builder(
                HTTP_TRANSPORT, jsonFactory,
                getCredentials(HTTP_TRANSPORT)
            ).setApplicationName("stock-discussion-analysis").build()
        }

    @Throws(IOException::class, GeneralSecurityException::class)
    fun uploadFile(targetFile: java.io.File): String {
        val itemCode = targetFile.parentFile.name
        var itemDirectoryId = findDirectory(itemCode)
        if (itemDirectoryId.contains(",")) {
            throw RuntimeException("Has multi item directory")
        } else if (itemDirectoryId.length < 1) {
            itemDirectoryId = createDirectory(itemCode)
        }
        val fileMetadata = File()
        fileMetadata.name = targetFile.name
        fileMetadata.mimeType = "text/csv"
        fileMetadata.parents = listOf(itemDirectoryId)
        val mediaContent = FileContent("text/csv", targetFile)
        val service = getService()
        val result = service.files().create(fileMetadata, mediaContent)
            .setFields("id")
            .execute()
        return result.id
    }
}