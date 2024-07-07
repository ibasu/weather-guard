package au.com.vanguard.weather.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -7153958279159676378L;
    protected int statusCode;
    private String fieldName;

    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(String msg, String fieldName) {
        super(msg);
        this.fieldName = fieldName;
    }

    public BaseException(String fieldName, String msg, Throwable t) {
        super(msg, t);
        this.fieldName = fieldName;
    }

}
