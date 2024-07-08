package com.example.mansshop_boot.domain.dto.admin.in;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdminProductImageDTO {

    private MultipartFile firstThumbnail;
    private String deleteFirstThumbnail;
    private List<MultipartFile> thumbnail;
    private List<String> deleteThumbnail;
    private List<MultipartFile> infoImage;
    private List<String> deleteInfoImage;
}
