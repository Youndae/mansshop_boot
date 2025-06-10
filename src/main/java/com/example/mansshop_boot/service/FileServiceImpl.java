package com.example.mansshop_boot.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("#{filePath['file.product.path']}")
    private String filePath;

    /**
     * AWS S3 사용시 필요
     */
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;
    private final AmazonS3Client amazonS3Client;

    /**
     *
     * @param image
     *
     * 파일 저장 처리.
     * 저장명을 반환.
     */
    @Override
    public String imageInsert(MultipartFile image) throws Exception{
        StringBuffer sb = new StringBuffer();
        String saveName = sb.append(new SimpleDateFormat("yyyyMMddHHmmss")
                        .format(System.currentTimeMillis()))
                .append(UUID.randomUUID().toString())
                .append(image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".")))
                .toString();
        String saveFile = filePath + saveName;

        image.transferTo(new File(saveFile));

        return saveName;
    }

    /**
     *
     * @param imageName
     *
     * 파일 삭제 처리.
     */
    @Override
    public void deleteImage(String imageName) {
        File file = new File(filePath + imageName);

        if(file.exists())
            file.delete();
    }

    /**
     *
     * @param image
     * S3에 파일 저장
     */
    /*
    @Override
    public String imageInsert(MultipartFile image) throws Exception{
        StringBuffer sb = new StringBuffer();
        String saveName = sb.append(new SimpleDateFormat("yyyyMMddHHmmss")
                        .format(System.currentTimeMillis()))
                .append(UUID.randomUUID().toString())
                .append(image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".")))
                .toString();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());

        try{
            amazonS3.putObject(
                    new PutObjectRequest(
                            bucket
                            , saveName
                            , image.getInputStream()
                            , objectMetadata
                    )
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        }catch (Exception e) {
            log.warn("productImage insert IOException");
            e.printStackTrace();
            throw new NullPointerException();
        }

        return saveName;
    }*/

    /**
     *
     *
     * S3 파일 삭제
     */
    /*
    @Override
    public void deleteImage(String imageName) {
        amazonS3.deleteObject(
                new DeleteObjectRequest(bucket, imageName)
        );
    }*/

    @Override
    public ResponseEntity<byte[]> getDisplayImage(String imageName) {
        File file = new File(filePath + imageName);
        ResponseEntity<byte[]> result = null;

        try {
            HttpHeaders header = new HttpHeaders();

            String contentType = "";
            if(imageName.endsWith(".png"))
                contentType = "image/png";
            else if(imageName.endsWith(".jpg") || imageName.endsWith(".jpeg"))
                contentType = "image/jpeg";
            else if (imageName.endsWith(".gif"))
                contentType = "image/gif";
            else
                contentType = "application/octet-stream";

            header.add("Content-Type", contentType);

            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                byte[] imageBytes = bis.readAllBytes();
                result = new ResponseEntity<>(imageBytes, header, HttpStatus.OK);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     *
     * @param imageName
     *
     * preSignedUrl로 반환.
     * 테스트를 위해 만료 시간은 1분으로 설정
     */
    /*@Override
    public URL getSignedUrl(String imageName) {

        Date expiration = new Date();
        long expirationTime = expiration.getTime();
        expirationTime += 1000 * 60 * 1; //1 minute 최종 설정은 테스트 해보고 결정
        expiration.setTime(expirationTime);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, imageName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);


        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    }*/

    /**
     *
     * @param imageName
     *
     * S3로부터 파일을 다운받아 InputStreamResource 타입으로 반환.
     * 프론트엔드에서는 blob으로 받아 처리.
     */
    @Override
    public ResponseEntity<InputStreamResource> getImageFile(String imageName) {
        S3Object s3Object = amazonS3.getObject(bucket, imageName);
        InputStreamResource resource = new InputStreamResource(s3Object.getObjectContent());

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(s3Object.getObjectMetadata().getContentLength())
                .body(resource);
    }
}
