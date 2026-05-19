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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "PublicContent")
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicContentController {

    private final NoticeRepository noticeRepository;
    private final FaqRepository faqRepository;
    private final EventRepository eventRepository;
    private final BannerRepository bannerRepository;
    private final PopupRepository popupRepository;

    @Operation(summary = "공지 목록 (pinned 우선)")
    @GetMapping("/notices")
    public Page<Notice> notices(Pageable pageable) {
        return noticeRepository.findByVisibleTrueOrderByPinnedDescCreatedAtDesc(pageable);
    }

    @Operation(summary = "공지 상세 — 조회수 +1")
    @GetMapping("/notices/{id}")
    @Transactional
    public Notice notice(@PathVariable Long id) {
        Notice n = noticeRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("NOTICE_NOT_FOUND", "공지를 찾을 수 없습니다."));
        if (!n.isVisible()) {
            throw ApiException.notFound("NOTICE_NOT_FOUND", "공지를 찾을 수 없습니다.");
        }
        n.increaseView();
        return n;
    }

    @Operation(summary = "FAQ 목록")
    @GetMapping("/faqs")
    public List<Faq> faqs(@RequestParam(required = false) String category) {
        if (category == null || category.isBlank()) {
            return faqRepository.findByVisibleTrueOrderBySortOrderAsc();
        }
        return faqRepository.findByCategoryAndVisibleTrueOrderBySortOrderAsc(category);
    }

    @Operation(summary = "진행 중 이벤트")
    @GetMapping("/events")
    public Page<Event> events(Pageable pageable) {
        return eventRepository.findOngoing(LocalDateTime.now(), pageable);
    }

    @Operation(summary = "배너 (placement별)")
    @GetMapping("/banners")
    public List<Banner> banners(@RequestParam Banner.Placement placement) {
        return bannerRepository.findByPlacementAndVisibleTrueOrderBySortOrderAsc(placement).stream()
                .filter(Banner::isCurrentlyVisible)
                .toList();
    }

    @Operation(summary = "현재 노출 팝업 목록")
    @GetMapping("/popups")
    public List<Popup> popups() {
        return popupRepository.findByVisibleTrueOrderBySortOrderAsc().stream()
                .filter(Popup::isCurrentlyVisible)
                .toList();
    }
}
