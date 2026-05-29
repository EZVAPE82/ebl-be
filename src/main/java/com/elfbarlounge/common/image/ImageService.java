package com.elfbarlounge.common.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 사용자 업로드 이미지 처리.
 *
 *  - 원본 저장 (lightbox / 상세 보기용, 풀 사이즈)
 *  - 800x800 center crop thumbnail (메인 그리드/리뷰 카드용, Shopify Dawn 패턴)
 *  - 300x300 center crop thumbnail (모바일 리스트용)
 *
 * 저장 경로: {localPath}/{yyyy/MM/dd}/{uuid}.{ext}
 *           {localPath}/{yyyy/MM/dd}/{uuid}_800.jpg
 *           {localPath}/{yyyy/MM/dd}/{uuid}_300.jpg
 *
 * 응답 URL: {publicBaseUrl}/{yyyy/MM/dd}/{uuid}*
 *
 * Thumbnailator center crop: Positions.CENTER + crop(size, size).
 * JPEG 0.85 품질 (시각 손실 거의 없음 + 파일 사이즈 ~70% 감소).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(ImageStorageProperties.class)
public class ImageService {

    private static final int THUMB_LG = 800;  // 메인 그리드용
    private static final int THUMB_SM = 300;  // 모바일 리스트용
    private static final double JPEG_QUALITY = 0.85;
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/heic"
    );

    private final ImageStorageProperties props;

    /**
     * 업로드된 사진 1장 처리.
     *  1. validation (사이즈 / mime type)
     *  2. 원본 저장 (확장자 보존)
     *  3. 800x800 center crop thumbnail 저장
     *  4. 300x300 center crop thumbnail 저장
     *  5. 결과 메타 반환
     */
    public UploadResult store(MultipartFile file) {
        validate(file);

        try {
            byte[] bytes = file.getBytes();
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(bytes));
            if (original == null) {
                throw new IllegalArgumentException("이미지 디코딩 실패 (지원하지 않는 형식)");
            }
            int width = original.getWidth();
            int height = original.getHeight();

            // 저장 경로 (날짜별 폴더)
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String ext = resolveExtension(file.getContentType());

            Path dir = Paths.get(props.localPath(), date);
            Files.createDirectories(dir);

            // 1. 원본 저장
            Path originalPath = dir.resolve(uuid + "." + ext);
            Files.write(originalPath, bytes);

            // 2. 800x800 center crop thumbnail (Shopify Dawn 패턴)
            Path thumbLgPath = dir.resolve(uuid + "_800.jpg");
            Thumbnails.of(new ByteArrayInputStream(bytes))
                    .size(THUMB_LG, THUMB_LG)
                    .crop(Positions.CENTER)
                    .outputFormat("jpg")
                    .outputQuality(JPEG_QUALITY)
                    .toFile(thumbLgPath.toFile());

            // 3. 300x300 center crop thumbnail
            Path thumbSmPath = dir.resolve(uuid + "_300.jpg");
            Thumbnails.of(new ByteArrayInputStream(bytes))
                    .size(THUMB_SM, THUMB_SM)
                    .crop(Positions.CENTER)
                    .outputFormat("jpg")
                    .outputQuality(JPEG_QUALITY)
                    .toFile(thumbSmPath.toFile());

            String urlBase = props.publicBaseUrl() + "/" + date + "/" + uuid;
            log.info("[image] uploaded {}x{} ({} KB) -> {}.{}",
                    width, height, bytes.length / 1024, urlBase, ext);

            return new UploadResult(
                    urlBase + "." + ext,
                    urlBase + "_800.jpg",
                    urlBase + "_300.jpg",
                    width,
                    height,
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException e) {
            log.error("[image] 저장 실패", e);
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기 초과 (최대 10MB)");
        }
        String type = file.getContentType();
        if (type == null || !ALLOWED_TYPES.contains(type.toLowerCase())) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식: " + type);
        }
    }

    private String resolveExtension(String contentType) {
        if (contentType == null) return "jpg";
        return switch (contentType.toLowerCase()) {
            case "image/png"  -> "png";
            case "image/webp" -> "webp";
            case "image/heic" -> "heic";
            default           -> "jpg";
        };
    }

    /** 업로드 결과 메타 — DB ReviewPhoto 에 그대로 매핑. */
    public record UploadResult(
            String url,
            String thumbnailUrl,
            String thumbnailSm,
            int width,
            int height,
            String contentType,
            long fileSize
    ) {}
}
