package woowacourse.exception.unauthorization;

public class LoginFailureException extends UnauthorizedException {
    private static final String DEFAULT_MESSAGE = "로그인에 실패했습니다";

    public LoginFailureException() {
        super(DEFAULT_MESSAGE);
    }
}
