package archivegarden.shop.service.user.token;

import archivegarden.shop.entity.auth.TokenType;
import archivegarden.shop.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {

    private final RedisUtil redisUtil;

    private static final long FIND_ACCOUNT_EXPIRE_SECONDS = 60 * 3L;   // 3분

    /**
     * 토큰 발급 및 Redis에 토큰 저장
     *
     * @param memberId 회원 ID
     * @param tokenType 토큰 타입
     * @return 발급된 토큰
     */
    public String issueToken(Long memberId, TokenType tokenType) {
        String token = UUID.randomUUID().toString();
        String key = tokenType.name() + ":" + token;
        redisUtil.saveData(key, String.valueOf(memberId), FIND_ACCOUNT_EXPIRE_SECONDS);
        return token;
    }

    /**
     * 토큰을 검증 후 삭제
     *
     * 없는/만료된/이미 사용된 토큰이면 Optional.empty()를 반환합니다.
     *
     * @param token 일회용 토큰
     * @param tokenType 토큰 타입
     * @return memberId(Optional) - 없으면 Optional.empty()
     */
    public Optional<Long> verifyAndUse(String token, TokenType tokenType) {
        String key = tokenType.name() + ":" + token;
        String memberIdStr = redisUtil.getData(key);
        if (memberIdStr == null) {
            return Optional.empty();
        }

        redisUtil.deleteData(key);

        return Optional.of(Long.parseLong(memberIdStr));
    }
}
