package woowacourse.exception;

public class TokenInvalidException extends IllegalArgumentException {
    private static final String DEFAULT_MESSAGE = "유효하지 않은 토큰입니다";

    public TokenInvalidException() {
        super(DEFAULT_MESSAGE);
    }
}
