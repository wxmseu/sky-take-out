package com.sky.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadFileService {
    String uploadFile(MultipartFile file);
}
