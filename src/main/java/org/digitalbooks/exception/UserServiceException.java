package org.digitalbooks.exception;

import lombok.Getter;

@Getter
public class UserServiceException extends RuntimeException {
    private long id = 1L;
    public UserServiceException(String message) {
        super(message);
    }
    public UserServiceException(Long id,String message) {
        super(message);
        this.id = id;
    }

}