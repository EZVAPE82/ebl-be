package com.elfbarlounge.domain.content.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByVisibleTrueOrderByPinnedDescCreatedAtDesc(Pageable pageable);
}
