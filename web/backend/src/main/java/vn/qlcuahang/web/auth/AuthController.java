package vn.qlcuahang.web.auth;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.qlcuahang.web.auth.AuthDtos.LoginRequest;
import vn.qlcuahang.web.auth.AuthDtos.LoginResponse;
import vn.qlcuahang.web.auth.AuthDtos.UserInfo;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req.maNhanVien(), req.matKhau());
    }

    @GetMapping("/me")
    public UserInfo me(Authentication auth) {
        return service.me(auth.getName());
    }
}
