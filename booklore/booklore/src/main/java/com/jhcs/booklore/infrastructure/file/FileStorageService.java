package com.jhcs.booklore.infrastructure.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.io.File.separator;
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${application.file.uploads.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(
            @NonNull MultipartFile sourceFIle,
             @NonNull Long id
                          ) {
        final String fileUploadSubPath = "users" + separator + id ;

        return uploadFile(sourceFIle,fileUploadSubPath);
    }

    private String uploadFile(
            @NonNull MultipartFile sourceFIle,
            @NonNull String fileUploadSubPath
                              ) {
        final String finalUploadPath = this.fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(fileUploadPath);
        if (!targetFolder.exists()) {
           boolean folderCreated = targetFolder.mkdirs();
              if (!folderCreated) {
               log.warn("Failed to create folder {}", targetFolder);
               return null;

              }
        }
        final String fileExtension = getFileExtension(sourceFIle.getOriginalFilename());
        final String targetFileName = finalUploadPath + separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Path.of(targetFileName);
        try {
            Files.write(targetPath, sourceFIle.getBytes());
            return targetFileName;
        } catch (IOException e) {
            log.error("File was not saved", e);

        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex+1).toLowerCase();
    }
}
