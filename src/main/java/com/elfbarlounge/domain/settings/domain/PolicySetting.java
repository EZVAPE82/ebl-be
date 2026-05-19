package com.elfbarlounge.domain.settings.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "policy_settings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PolicySetting {

    @Id
    @Column(name = "setting_key", length = 60)
    private String key;

    @Column(name = "setting_value", length = 200, nullable = false)
    private String value;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PolicySetting(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String value) {
        this.value = value;
        this.updatedAt = LocalDateTime.now();
    }
}
