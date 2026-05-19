package com.elfbarlounge.domain.member.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.common.security.AuthPrincipal;
import com.elfbarlounge.domain.member.application.AddressService;
import com.elfbarlounge.domain.member.domain.MemberAddress;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Tag(name = "MemberAddress")
@RestController
@RequestMapping("/api/v1/members/me/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "내 배송지 목록 (기본 배송지 우선)")
    @GetMapping
    public List<AddressView> list(@AuthenticationPrincipal AuthPrincipal principal) {
        return addressService.list(require(principal)).stream().map(AddressView::from).toList();
    }

    @Operation(summary = "배송지 등록 (최대 5개)")
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody AddressRequest req
    ) {
        Long id = addressService.create(require(principal), toInput(req));
        return ResponseEntity.created(URI.create("/api/v1/members/me/addresses/" + id))
                .body(Map.of("id", id));
    }

    @Operation(summary = "배송지 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long id, @Valid @RequestBody AddressRequest req
    ) {
        addressService.update(require(principal), id, toInput(req));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "배송지 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable Long id) {
        addressService.delete(require(principal), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "기본 배송지로 설정")
    @PostMapping("/{id}/default")
    public ResponseEntity<Void> setDefault(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable Long id) {
        addressService.setDefault(require(principal), id);
        return ResponseEntity.noContent().build();
    }

    public record AddressRequest(
            @Size(max = 40) String label,
            @NotBlank @Size(max = 80) String recipientName,
            @NotBlank @Size(max = 32) String phone,
            @NotBlank @Size(max = 16) String postalCode,
            @NotBlank @Size(max = 200) String address1,
            @Size(max = 200) String address2,
            boolean isDefault
    ) {}

    public record AddressView(
            Long id, String label, String recipientName, String phoneMasked,
            String postalCode, String address1, String address2, boolean isDefault
    ) {
        public static AddressView from(MemberAddress a) {
            return new AddressView(
                    a.getId(), a.getLabel(), a.getRecipientName(),
                    maskPhone(a.getPhone()),
                    a.getPostalCode(), a.getAddress1(), a.getAddress2(),
                    a.isDefault()
            );
        }
        private static String maskPhone(String p) {
            if (p == null) return null;
            String d = p.replaceAll("[^0-9]", "");
            if (d.length() < 8) return "***";
            return d.substring(0, 3) + "-****-" + d.substring(d.length() - 4);
        }
    }

    private AddressService.AddressInput toInput(AddressRequest r) {
        return new AddressService.AddressInput(
                r.label(), r.recipientName(), r.phone(),
                r.postalCode(), r.address1(), r.address2(), r.isDefault()
        );
    }

    private Long require(AuthPrincipal p) {
        if (p == null) throw ApiException.unauthorized("UNAUTHENTICATED", "로그인이 필요합니다.");
        return p.memberId();
    }
}
