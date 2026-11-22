package vn.qlcuahang.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Mô tả API cho Swagger UI; nút Authorize nhận token lấy từ POST /api/auth/login. */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Quản lý cửa hàng thực phẩm")
                        .version("1.0")
                        .description("Đăng nhập bằng POST /api/auth/login (VD admin/admin), chép token vào nút "
                                + "Authorize. Vai trò QL (quản lý) dùng được mọi API; NV (nhân viên) không xem "
                                + "báo cáo, nhân viên và không sửa sản phẩm, khách hàng."))
                .components(new Components().addSecuritySchemes("bearer", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer"));
    }
}
