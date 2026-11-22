package vn.qlcuahang.web.security;

import java.time.Instant;
import java.util.List;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import vn.qlcuahang.web.config.AppProperties;

@Service
public class JwtService {

    public record IssuedToken(String token, Instant expiresAt) {
    }

    private final JwtEncoder encoder;
    private final AppProperties props;

    public JwtService(JwtEncoder encoder, AppProperties props) {
        this.encoder = encoder;
        this.props = props;
    }

    public IssuedToken issue(String maNhanVien, String hoTen, Role role) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.jwt().ttl());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.jwt().issuer())
                .subject(maNhanVien)
                .issuedAt(now)
                .expiresAt(exp)
                .claim("name", hoTen)
                .claim("roles", List.of(role.authority()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new IssuedToken(token, exp);
    }
}
