package com.elfbarlounge.common.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

/**
 * httpOnly 쿠키 발급/읽기 헬퍼.
 *
 * 보안 정책 (rule 1, 6, 12):
 *  - httpOnly: XSS 환경에서도 JS 접근 불가
 *  - Secure: HTTPS only (운영)
 *  - SameSite=Strict: CSRF 1차 방어
 *  - Path=/: 모든 경로
 *
 * 쿠키 이름:
 *  - eb_at: access token  (회원)
 *  - eb_rt: refresh token (회원)
 *  - eb_aat: admin access token (어드민, refresh 없음)
 */
public final class CookieUtil {

    public static final String COOKIE_ACCESS = "eb_at";
    public static final String COOKIE_REFRESH = "eb_rt";
    public static final String COOKIE_ADMIN_ACCESS = "eb_aat";

    private CookieUtil() {}

    /** 보호된 쿠키 발급. maxAgeSeconds 음수면 세션 쿠키. */
    public static void setSecureCookie(HttpServletResponse res,
                                       String name,
                                       String value,
                                       long maxAgeSeconds,
                                       boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
        res.addHeader("Set-Cookie", cookie.toString());
    }

    /** 쿠키 즉시 만료. */
    public static void expireCookie(HttpServletResponse res, String name, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        res.addHeader("Set-Cookie", cookie.toString());
    }

    /** 요청에서 쿠키 값 추출. */
    public static String readCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
