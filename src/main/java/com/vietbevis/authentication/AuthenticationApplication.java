package com.vietbevis.authentication;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
public class AuthenticationApplication {

    @Value("${spring.application.name:Authentication Service}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:/api/auth}")
    private String contextPath;

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationInfoLogger(Environment environment) {
        return (ApplicationArguments args) -> {
            try {
                Thread.sleep(500);

                String hostAddress = InetAddress.getLocalHost().getHostAddress();
                String hostName = InetAddress.getLocalHost().getHostName();
                String formattedContextPath =
                    contextPath.startsWith("/") ? contextPath : "/" + contextPath;
                String baseUrl = String.format("http://%s:%s%s", hostAddress, serverPort,
                    formattedContextPath);
                String swaggerUrl = String.format("%s/swagger-ui.html", baseUrl);
                String actuatorUrl = String.format("%s/actuator", baseUrl);
                String activeProfiles = environment.getActiveProfiles().length > 0
                    ? String.join(", ", environment.getActiveProfiles())
                    : "default";

                log.info("""
                        ----------------------------------------------------------
                        ⭐ {} đã khởi động thành công! ⭐
                        ----------------------------------------------------------
                        📅 Thời gian khởi động: {}
                        🖥️ Hostname: {}
                        🌐 IP: {}
                        🔌 Port: {}
                        🔗 Context Path: {}
                        ⚙️ Profiles đang hoạt động: {}
                        📊 Truy cập: {}
                        📝 API Docs: {}
                        📈 Actuator: {}
                        ----------------------------------------------------------""",
                    applicationName,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                    hostName,
                    hostAddress,
                    serverPort,
                    formattedContextPath,
                    activeProfiles,
                    baseUrl,
                    swaggerUrl,
                    actuatorUrl);

            } catch (Exception e) {
                log.error("Không thể hiển thị thông tin khởi động", e);
            }
        };
    }

}
