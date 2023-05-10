package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CommonUtil {
    public static boolean validateSrcAndTargetVersions(String srcVersion, String targetVersion) {
        if (Arrays.asList(Constants.supportedSourceVersions).contains(srcVersion) &&
                Arrays.asList(Constants.supportedTargetVersions).contains(targetVersion)) {
            return true;
        } else {
            return false;
        }
    }

    public static void cleanDirectory(String pathToDirectory) throws CTLArtifactConversionException {
        File directory = new File(pathToDirectory);
        try {
            if (directory.exists()) {
                FileUtils.deleteDirectory(directory);
            }
            Files.createDirectories(Paths.get(pathToDirectory));
        } catch (IOException e) {
            String msg = "Error while cleaning Docs directory at " + directory;
            throw new CTLArtifactConversionException(msg, e);
        }
    }

    /**
     * Write content to file.
     *
     * @param path    Location of the file
     * @param content Content to be written
     * @throws CTLArtifactConversionException If an error occurs while writing to file
     */
    public static void writeFile(String path, String content) throws CTLArtifactConversionException {

        try (FileWriter writer = new FileWriter(path)) {
            IOUtils.copy(new StringReader(content), writer);
        } catch (IOException e) {
            String errorMessage = "I/O error while writing to file: " + path;
            throw new CTLArtifactConversionException(errorMessage, e);
        }
    }

    /**
     * Create temporary directory in temporary location.
     *
     * @throws CTLArtifactConversionException If an error occurs while creating temporary location
     */
    public static File createTempDirectory() throws CTLArtifactConversionException {

        String currentDirectory = System.getProperty(Constants.JAVA_IO_TMPDIR);
        String tmpDirectoryName = RandomStringUtils.randomAlphanumeric(Constants.TEMP_FILENAME_LENGTH);
        File tempDirectory = new File(currentDirectory + File.separator + tmpDirectoryName);
        createDirectory(tempDirectory.getPath());
        return tempDirectory;
    }

    /**
     * Create directory at the given path.
     *
     * @param path Path of the directory
     * @throws CTLArtifactConversionException If directory creation failed
     */
    public static void createDirectory(String path) throws CTLArtifactConversionException {

        if (path != null) {
            File file = new File(path);
            if (!file.exists() && !file.mkdirs()) {
                String errorMessage = "Error while creating directory : " + path;
                throw new CTLArtifactConversionException(errorMessage);
            }
        }
    }

    /**
     * This method uploads a given file to specified location
     *
     * @param uploadedInputStream input stream of the file
     * @param newFileName         name of the file to be created
     * @param storageLocation     destination of the new file
     * @throws CTLArtifactConversionException If the file transfer fails
     */
    public static void transferFile(InputStream uploadedInputStream, String newFileName, String storageLocation)
            throws CTLArtifactConversionException {

        try (FileOutputStream outFileStream = new FileOutputStream(new File(storageLocation, newFileName))) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outFileStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            String errorMessage = "Error in transferring files.";
            throw new CTLArtifactConversionException(errorMessage, e);
        }
    }

    /**
     * Preprocess the imported artifact by removing the unnecessary files and folders and extracts it to destination folder.
     *
     * @param sourceFile  Imported archive file
     * @return destination folder name
     * @throws CTLArtifactConversionException If an error occurs while deleting the unnecessary files and folders
     */
    public static String extractArchive(File sourceFile, String destination) throws CTLArtifactConversionException {
        String archiveName = null;
        try (ZipFile zipFile = new ZipFile(sourceFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                String fileName = zipEntry.getName();
                int index = 0;

                //This index variable is used to get the extracted folder name; that is root directory
                if (index == 0) {
                    if (fileName.contains("/")) {
                        archiveName = fileName
                                .substring(0, fileName.indexOf(Constants.ZIP_FILE_SEPARATOR));
                    } else if (fileName.contains("\\")) {
                        archiveName = fileName
                                .substring(0, fileName.indexOf(Constants.WIN_ZIP_FILE_SEPARATOR));

                    }
                    --index;
                }

                //prevent zip slip  https://snyk.io/research/zip-slip-vulnerability
                File destinationFile = new File(destination, zipEntry.getName());
                String canonicalDestinationFilePath = destinationFile.getCanonicalPath();
                if (!canonicalDestinationFilePath.startsWith(new File(destination).getCanonicalPath())) {
                    String errorMessage = "Attempt to upload invalid zip archive with file at " + zipEntry.getName()
                            + ". File path is outside target directory";
                    throw new CTLArtifactConversionException(errorMessage);
                }

                File archiveFile = new File(destination + File.separator + fileName);
                if (zipEntry.isDirectory()) {
                    archiveFile.mkdirs();
                } else {
                    archiveFile.getParentFile().mkdirs();
                    try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
                        FileUtils.copyInputStreamToFile(inputStream, destinationFile);
                    }
                }
            }
            return archiveName;
        } catch (IOException e) {
            throw new CTLArtifactConversionException("Error while extracting archive", e);
        }
    }

    public static JsonObject convertMapToJsonObject(Map map) {
        Gson gson = new Gson();
        JsonObject json = gson.toJsonTree(map).getAsJsonObject();
        return json;
    }
}
