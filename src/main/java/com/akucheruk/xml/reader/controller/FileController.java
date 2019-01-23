package com.akucheruk.xml.reader.controller;

import org.springframework.web.multipart.MultipartFile;

public interface FileController {
    String handleFileUpload(MultipartFile file);
    String uploadFile();
}
