package com.sky.service.impl;

import com.sky.service.UploadFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class UploadFileServiceImpl implements UploadFileService {

    public static final String UPLOAD_PATH = "C:\\Users\\xiaom\\Pictures\\project_images";

    @Override
    public String uploadFile(MultipartFile file) {
        // 原始文件名称
        String originalFileName = file.getOriginalFilename();
        // 截取原始文件名称的后缀,2222.jpg
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // 构造新文件名称

        String objName = UUID.randomUUID().toString() + extension;
        log.info("新文件名称为{}", objName);
        try {
            return saveFile(file.getBytes(), objName);

        } catch (IOException e) {
            log.info("获取文件流失败{}" ,e.getMessage());
        }
        return null;
    }

    public String saveFile(byte[] bytes, String fileName) throws IOException {
        String filePathName = UPLOAD_PATH + "\\" +fileName;
        Path path = Paths.get(filePathName);
        log.info("当前存储的路径为{}", path);
        try {
            Files.write(path, bytes);
            return filePathName;
        } catch (IOException e) {
            log.info("文件上传失败，原因为{}" ,e.getMessage());
        }
        return null;
    }
}
