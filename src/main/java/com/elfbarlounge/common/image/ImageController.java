package com.elfbarlounge.common.image;

import com.elfbarlounge.common.image.ImageService.UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 사용자 사진 업로드 API.
 *
 * POST /api/v1/me/images           — 1 장 업로드
 * POST /api/v1/me/images/batch     — 여러 장 업로드 (후기 작성용)
 *
 * 인증: 회원 (SecurityConfig 의 /api/v1/me/** 패턴에 따름)
 * 사용처: 후기 작성, 마이페이지 프로필 등
 *
 * 응답 (UploadResult): url, thumbnailUrl, thumbnailSm, width, height, ...
 * 프론트는 응답의 thumbnailUrl 을 메인 리스트에, url 을 lightbox 풀 사이즈에 사용.
 */
@RestController
@RequestMapping("/api/v1/me/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UploadResult> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(imageService.store(file));
    }

    @PostMapping("/batch")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UploadResult>> uploadBatch(@RequestParam("files") MultipartFile[] files) {
        return ResponseEntity.ok(Arrays.stream(files).map(imageService::store).toList());
    }
}
