package com.elfbarlounge.domain.member.domain;

/**
 * 등급 체계. 6개월 누적 결제액 기준.
 * 기능명세서 v1.5 3.2.
 */
public enum GradeCode {
    ENTRY(0, 0.0, 0.0),
    TIER_10K(70_000, 0.0, 0.005),
    TIER_25K(150_000, 0.01, 0.005),
    TIER_40K(300_000, 0.015, 0.005),
    VIP(500_000, 0.02, 0.005);

    public final long minAccumulated;
    public final double earnRate;
    public final double vbankBonus;

    GradeCode(long minAccumulated, double earnRate, double vbankBonus) {
        this.minAccumulated = minAccumulated;
        this.earnRate = earnRate;
        this.vbankBonus = vbankBonus;
    }

    public static GradeCode fromAmount(long amount) {
        GradeCode result = ENTRY;
        for (GradeCode g : values()) {
            if (amount >= g.minAccumulated) {
                result = g;
            }
        }
        return result;
    }
}
