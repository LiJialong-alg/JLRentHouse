package org.example.lease.common.exception;

import lombok.Data;

@Data
public class LeaseException extends RuntimeException {
    private Integer code;

    public LeaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
