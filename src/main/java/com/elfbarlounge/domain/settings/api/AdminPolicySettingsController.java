package com.elfbarlounge.domain.settings.api;

import com.elfbarlounge.domain.settings.application.PolicySettingsService;
import com.elfbarlounge.domain.settings.domain.PolicySetting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "AdminPolicy")
@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
public class AdminPolicySettingsController {

    private final PolicySettingsService service;

    @Operation(summary = "[Admin] 정책 설정값 전체 조회")
    @GetMapping
    public List<PolicySetting> all() {
        return service.all();
    }

    @Operation(summary = "[Admin] 정책 설정값 변경")
    @PutMapping("/{key}")
    public ResponseEntity<Void> update(@PathVariable String key, @Valid @RequestBody UpdateRequest req) {
        service.update(key, req.value());
        return ResponseEntity.noContent().build();
    }

    public record UpdateRequest(@NotBlank @Size(max = 200) String value) {}
}
