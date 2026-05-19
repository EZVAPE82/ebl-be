package com.elfbarlounge.domain.content.api;

import com.elfbarlounge.common.exception.ApiException;
import com.elfbarlounge.domain.content.domain.Banner;
import com.elfbarlounge.domain.content.domain.BannerRepository;
import com.elfbarlounge.domain.content.domain.Event;
import com.elfbarlounge.domain.content.domain.EventRepository;
import com.elfbarlounge.domain.content.domain.Faq;
import com.elfbarlounge.domain.content.domain.FaqRepository;
import com.elfbarlounge.domain.content.domain.Notice;
import com.elfbarlounge.domain.content.domain.NoticeRepository;
import com.elfbarlounge.domain.content.domain.Popup;
import com.elfbarlounge.domain.content.domain.PopupRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "AdminContent")
@RestController
@RequestMapping("/api/v1/admin/content")
@RequiredArgsConstructor
public class AdminContentController {

    private final NoticeRepository noticeRepository;
    private final FaqRepository faqRepository;
    private final EventRepository eventRepository;
    private final BannerRepository bannerRepository;
    private final PopupRepository popupRepository;

    // ----- Notice -----
    @Operation(summary = "[Admin] 공지 생성")
    @PostMapping("/notices")
    public ResponseEntity<Map<String, Object>> createNotice(@Valid @RequestBody NoticeReq r) {
        Long id = noticeRepository.save(Notice.builder()
                .title(r.title()).content(r.content()).pinned(r.pinned()).visible(r.visible()).build()).getId();
        return ResponseEntity.created(URI.create("/api/v1/admin/content/notices/" + id)).body(Map.of("id", id));
    }

    @PutMapping("/notices/{id}")
    @Transactional
    public ResponseEntity<Void> updateNotice(@PathVariable Long id, @Valid @RequestBody NoticeReq r) {
        Notice n = noticeRepository.findById(id).orElseThrow(() -> ApiException.notFound("NOTICE_NOT_FOUND", "공지를 찾을 수 없습니다."));
        n.update(r.title(), r.content(), r.pinned() != null && r.pinned(), r.visible() == null || r.visible());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/notices/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notices")
    public Page<Notice> listNotices(Pageable pageable) { return noticeRepository.findAll(pageable); }

    // ----- FAQ -----
    @PostMapping("/faqs")
    public ResponseEntity<Map<String, Object>> createFaq(@Valid @RequestBody FaqReq r) {
        Long id = faqRepository.save(Faq.builder()
                .category(r.category()).question(r.question()).answer(r.answer())
                .sortOrder(r.sortOrder()).visible(r.visible()).build()).getId();
        return ResponseEntity.created(URI.create("/api/v1/admin/content/faqs/" + id)).body(Map.of("id", id));
    }

    @PutMapping("/faqs/{id}")
    @Transactional
    public ResponseEntity<Void> updateFaq(@PathVariable Long id, @Valid @RequestBody FaqReq r) {
        Faq f = faqRepository.findById(id).orElseThrow(() -> ApiException.notFound("FAQ_NOT_FOUND", "FAQ를 찾을 수 없습니다."));
        f.update(r.category(), r.question(), r.answer(),
                r.sortOrder() != null ? r.sortOrder() : 0, r.visible() == null || r.visible());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/faqs/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        faqRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ----- Event -----
    @PostMapping("/events")
    public ResponseEntity<Map<String, Object>> createEvent(@Valid @RequestBody EventReq r) {
        Long id = eventRepository.save(Event.builder()
                .title(r.title()).summary(r.summary()).content(r.content())
                .bannerUrl(r.bannerUrl()).startsAt(r.startsAt()).endsAt(r.endsAt())
                .visible(r.visible()).build()).getId();
        return ResponseEntity.created(URI.create("/api/v1/admin/content/events/" + id)).body(Map.of("id", id));
    }

    @PutMapping("/events/{id}")
    @Transactional
    public ResponseEntity<Void> updateEvent(@PathVariable Long id, @Valid @RequestBody EventReq r) {
        Event e = eventRepository.findById(id).orElseThrow(() -> ApiException.notFound("EVENT_NOT_FOUND", "이벤트를 찾을 수 없습니다."));
        e.update(r.title(), r.summary(), r.content(), r.bannerUrl(),
                r.startsAt(), r.endsAt(), r.visible() == null || r.visible());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ----- Banner -----
    @PostMapping("/banners")
    public ResponseEntity<Map<String, Object>> createBanner(@Valid @RequestBody BannerReq r) {
        Long id = bannerRepository.save(Banner.builder()
                .placement(r.placement()).imageUrl(r.imageUrl()).linkUrl(r.linkUrl()).altText(r.altText())
                .sortOrder(r.sortOrder()).visible(r.visible()).startsAt(r.startsAt()).endsAt(r.endsAt()).build()).getId();
        return ResponseEntity.created(URI.create("/api/v1/admin/content/banners/" + id)).body(Map.of("id", id));
    }

    @PutMapping("/banners/{id}")
    @Transactional
    public ResponseEntity<Void> updateBanner(@PathVariable Long id, @Valid @RequestBody BannerReq r) {
        Banner b = bannerRepository.findById(id).orElseThrow(() -> ApiException.notFound("BANNER_NOT_FOUND", "배너를 찾을 수 없습니다."));
        b.update(r.placement(), r.imageUrl(), r.linkUrl(), r.altText(),
                r.sortOrder() != null ? r.sortOrder() : 0, r.visible() == null || r.visible(),
                r.startsAt(), r.endsAt());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/banners/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        bannerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ----- Popup -----
    @PostMapping("/popups")
    public ResponseEntity<Map<String, Object>> createPopup(@Valid @RequestBody PopupReq r) {
        Long id = popupRepository.save(Popup.builder()
                .title(r.title()).imageUrl(r.imageUrl()).linkUrl(r.linkUrl()).contentHtml(r.contentHtml())
                .sortOrder(r.sortOrder()).visible(r.visible()).startsAt(r.startsAt()).endsAt(r.endsAt()).build()).getId();
        return ResponseEntity.created(URI.create("/api/v1/admin/content/popups/" + id)).body(Map.of("id", id));
    }

    @PutMapping("/popups/{id}")
    @Transactional
    public ResponseEntity<Void> updatePopup(@PathVariable Long id, @Valid @RequestBody PopupReq r) {
        Popup p = popupRepository.findById(id).orElseThrow(() -> ApiException.notFound("POPUP_NOT_FOUND", "팝업을 찾을 수 없습니다."));
        p.update(r.title(), r.imageUrl(), r.linkUrl(), r.contentHtml(),
                r.sortOrder() != null ? r.sortOrder() : 0, r.visible() == null || r.visible(),
                r.startsAt(), r.endsAt());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/popups/{id}")
    public ResponseEntity<Void> deletePopup(@PathVariable Long id) {
        popupRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ===== DTOs =====
    public record NoticeReq(
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(max = 50000) String content,
            Boolean pinned, Boolean visible
    ) {}
    public record FaqReq(
            @Size(max = 40) String category,
            @NotBlank @Size(max = 300) String question,
            @NotBlank @Size(max = 50000) String answer,
            Integer sortOrder, Boolean visible
    ) {}
    public record EventReq(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 500) String summary,
            @Size(max = 50000) String content,
            @Size(max = 1000) String bannerUrl,
            LocalDateTime startsAt, LocalDateTime endsAt,
            Boolean visible
    ) {}
    public record BannerReq(
            @NotNull Banner.Placement placement,
            @NotBlank @Size(max = 1000) String imageUrl,
            @Size(max = 1000) String linkUrl,
            @Size(max = 200) String altText,
            Integer sortOrder, Boolean visible,
            LocalDateTime startsAt, LocalDateTime endsAt
    ) {}
    public record PopupReq(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 1000) String imageUrl,
            @Size(max = 1000) String linkUrl,
            @Size(max = 50000) String contentHtml,
            Integer sortOrder, Boolean visible,
            LocalDateTime startsAt, LocalDateTime endsAt
    ) {}
}
