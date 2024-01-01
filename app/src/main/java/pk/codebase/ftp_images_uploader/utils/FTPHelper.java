package pk.codebase.ftp_images_uploader.utils;

import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPHelper {

    private static final String FTP_SERVER = "stoneftp.mynetgear.com";
    private static final int FTP_PORT = 21;
    private static final String FTP_USERNAME = "stonebackup";
    private static final String FTP_PASSWORD = "0X9>haIj";

    public static FTPClient connectToFTP() {
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(FTP_SERVER, FTP_PORT);
            ftpClient.login(FTP_USERNAME, FTP_PASSWORD);

            // Use passive mode if needed
            ftpClient.enterLocalPassiveMode();

            // Set file type to binary
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Get the list of remote folders
            String[] remoteFolders = ftpClient.listNames();

            // Print the list of remote folders
            if (remoteFolders != null) {
                for (String folder : remoteFolders) {
                    System.out.println(folder);
                }
            } else {
                System.out.println("No remote folders found.");
            }

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                throw new IOException("Failed to connect to the FTP server. Reply code: " + replyCode);
            }

            System.out.println("Connected to the FTP server");

            return ftpClient;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void disconnectFromFTP(FTPClient ftpClient) {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
                System.out.println("Disconnected from the FTP server");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void uploadFile(String remoteFilePath, String localFilePath) {
        FTPClient ftpClient = new FTPClient();
        FileInputStream fileInputStream = null;

        try {
            ftpClient.connect(FTP_SERVER, FTP_PORT);
            ftpClient.login(FTP_USERNAME, FTP_PASSWORD);

            // Set FTP passive mode (optional, may be required depending on the server)
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory("/stone/");

            fileInputStream = new FileInputStream(new File(localFilePath));

            boolean uploaded = ftpClient.storeFile(remoteFilePath, fileInputStream);
            if (uploaded) {
                System.out.println("File uploaded successfully.");
            } else {
                System.out.println("Error uploading file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
