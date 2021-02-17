package crawler.manager.file;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

public class GoogleDriveManager {
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String discussionDirectoryId = "1NvZhfU-8D7SQurvt0CZtP2TA-Er7JADs";
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA);
//    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";
    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();


        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static String findDirectory(String fileName) throws GeneralSecurityException, IOException {
        StringBuilder resultStr = new StringBuilder();
        Drive service = getService();

        FileList files = service.files().list()
                .setQ("mimeType='application/vnd.google-apps.folder' and trashed = false and name='" + fileName + "'")
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
                .execute();


        for (com.google.api.services.drive.model.File file : files.getFiles()) {
            resultStr.append(",").append(file.getId());
        }

        if (resultStr.length() == 0) {
            return "";
        }

        return resultStr.substring(1);
    }

    public static String createDirectory(String directoryName) throws GeneralSecurityException, IOException {
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(directoryName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(discussionDirectoryId));


        Drive service = getService();


        com.google.api.services.drive.model.File result = service.files().create(fileMetadata)
                .setFields("id")
                .execute();




        return result.getId();
    }

    private static Drive getService() throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();


        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                GoogleDriveManager.getCredentials(HTTP_TRANSPORT)).setApplicationName("stock-discussion-analysis").build();
    }

    public static String uploadFile(File targetFile) throws IOException, GeneralSecurityException {

        String itemCode = targetFile.getParentFile().getName();

        String itemDirectoryId = findDirectory(itemCode);

        if (itemDirectoryId.contains(",")) {
            throw new RuntimeException("Has multi item directory");
        } else if (itemDirectoryId.length() < 1) {
            itemDirectoryId = createDirectory(itemCode);
        }

        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(targetFile.getName());
        fileMetadata.setMimeType("text/csv");
        fileMetadata.setParents(Collections.singletonList(itemDirectoryId));

        FileContent mediaContent = new FileContent("text/csv", targetFile);

        Drive service = getService();

        com.google.api.services.drive.model.File result = service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        return result.getId();

    }

    public static JsonFactory getJsonFactory() { return JSON_FACTORY; }

}
