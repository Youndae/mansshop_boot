package com.example.mansshop_boot.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String imageInsert(MultipartFile image) throws Exception;

    void deleteImage(String imageName);

    ResponseEntity<byte[]> getDisplayImage(String imageName);

    /*URL getSignedUrl(String imageName);*/ // S3에서 이미지 파일 가져오는 메서드

    ResponseEntity<InputStreamResource> getImageFile(String imageName);
}
