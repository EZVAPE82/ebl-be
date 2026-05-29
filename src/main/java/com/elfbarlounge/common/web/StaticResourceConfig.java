package com.elfbarlounge.common.web;

import com.elfbarlounge.common.image.ImageStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * 업로드 디렉토리를 정적 리소스로 노출.
 *
 * mode=local (개발/초기 운영): {publicBaseUrl}/** -> file:{localPath}/
 * mode=s3   (향후 운영):       S3 직접 서빙 (이 설정 무시됨)
 *
 * 운영 nginx 에서는 Spring 의 정적 서빙을 거치지 않고 nginx 가 직접 파일을 서빙하는 게
 * 효율적이지만, 초기엔 Spring 으로 동작 확인 + 단순화.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ImageStorageProperties.class)
public class StaticResourceConfig implements WebMvcConfigurer {

    private final ImageStorageProperties props;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!"local".equalsIgnoreCase(props.mode())) {
            return; // S3 모드면 이 매핑 불필요
        }
        // /uploads/** -> file:/var/uploads/  (env 에 따라 변경 가능)
        // base URL 의 마지막 path segment 만 추출해서 라우트 패턴으로 사용.
        String publicBase = props.publicBaseUrl();
        String pattern = extractPathSegment(publicBase) + "/**";
        String location = "file:" + Paths.get(props.localPath()).toAbsolutePath().normalize() + "/";

        registry.addResourceHandler(pattern)
                .addResourceLocations(location)
                .setCachePeriod(60 * 60 * 24 * 30); // 30일 캐시 (사진은 immutable UUID 파일명)
    }

    /** "/uploads" or "https://api.example.com/uploads" -> "/uploads" */
    private String extractPathSegment(String url) {
        if (url == null || url.isBlank()) return "/uploads";
        int idx = url.indexOf("://");
        if (idx > 0) {
            int slash = url.indexOf('/', idx + 3);
            return slash > 0 ? url.substring(slash) : "/uploads";
        }
        return url.startsWith("/") ? url : "/" + url;
    }
}
