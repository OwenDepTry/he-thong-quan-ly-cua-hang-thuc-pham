package vn.qlcuahang.web.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/** Người đang gọi API, đọc từ JWT đã được Spring Security kiểm tra. */
public record CurrentUser(String maNhanVien, Role role) {

    public static CurrentUser from(Authentication auth) {
        boolean ql = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(Role.QL.authority()::equals);
        return new CurrentUser(auth.getName(), ql ? Role.QL : Role.NV);
    }

    public boolean isQuanLy() {
        return role == Role.QL;
    }
}
