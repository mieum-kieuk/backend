package archivegarden.shop.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "API 에러 응답 DTO")
public class ErrorResponseDto {

    private String code;
    private String message;
    private String path;
    private Instant timestamp;
}

