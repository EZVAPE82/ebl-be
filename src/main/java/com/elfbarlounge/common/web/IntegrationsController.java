package com.elfbarlounge.common.web;

import com.elfbarlounge.common.config.IntegrationProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 외부 연동 활성화 상태 노출 (공개).
 * 프론트엔드가 로그인 페이지 네이버 버튼 등 조건부 렌더링에 사용.
 *
 * 보안: 토글 boolean만 노출, 클라이언트 키·시크릿은 절대 X (rule 3).
 */
@Tag(name = "Integrations")
@RestController
@RequestMapping("/api/v1/public/integrations")
@RequiredArgsConstructor
public class IntegrationsController {

    private final IntegrationProperties props;

    @Operation(summary = "외부 연동 활성화 상태")
    @GetMapping
    public Map<String, Boolean> status() {
        return Map.of(
                "naverLogin", props.naverLogin().enabled(),
                "naverCommerce", props.naverCommerce().enabled(),
                "elevenOpenapi", props.elevenOpenapi().enabled()
        );
    }
}
