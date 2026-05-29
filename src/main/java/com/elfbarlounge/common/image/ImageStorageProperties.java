package com.elfbarlounge.common.image;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 이미지 저장소 설정.
 *
 *  - mode: "local" (개발/초기 운영, 로컬 파일시스템) | "s3" (운영, AWS S3)
 *  - localPath: local 모드일 때 저장 디렉토리 (서버 컨테이너 내부 경로)
 *  - publicBaseUrl: 클라이언트가 접근하는 base URL (예: https://api.elfbarlounge.com/uploads)
 *
 * application.yml 에서 override:
 *   app:
 *     image:
 *       mode: local
 *       local-path: /var/uploads
 *       public-base-url: https://api.elfbarlounge.com/uploads
 */
@ConfigurationProperties(prefix = "app.image")
public record ImageStorageProperties(
        String mode,
        String localPath,
        String publicBaseUrl
) {
    public ImageStorageProperties {
        if (mode == null || mode.isBlank()) mode = "local";
        if (localPath == null || localPath.isBlank()) localPath = "./uploads";
        if (publicBaseUrl == null || publicBaseUrl.isBlank()) publicBaseUrl = "/uploads";
    }
}
