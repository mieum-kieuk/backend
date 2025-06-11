package archivegarden.shop.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing(dateTimeProviderRef = "fixedDateTimeProvider")
public class TestAuditingConfig {

    public static LocalDateTime CURRENT_TEST_DATE_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @Bean(name = "fixedDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.of(CURRENT_TEST_DATE_TIME, ZoneOffset.UTC));
    }

    public static void advanceSeconds(long seconds) {
        CURRENT_TEST_DATE_TIME = CURRENT_TEST_DATE_TIME.plusSeconds(seconds);
    }

    public static void advanceMonth(int months) {
        CURRENT_TEST_DATE_TIME = CURRENT_TEST_DATE_TIME.plusMonths(months);
    }

    public static void setTime(LocalDateTime dateTime) {
        CURRENT_TEST_DATE_TIME = dateTime;
    }
}