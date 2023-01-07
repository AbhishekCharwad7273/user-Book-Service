package org.digitalbooks.controller;

import org.digitalbooks.exception.ErrorInfo;
import org.digitalbooks.exception.UserServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@CrossOrigin(origins = "*")
public class UserControllerAdvice {
    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ErrorInfo> handleBookServiceException(UserServiceException ex){
        ErrorInfo errorInfo = ErrorInfo.builder().errorId(ex.getId()).errorMessage(ex.getMessage()).build();
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGeneralError(Exception ex){
        ErrorInfo errorInfo = ErrorInfo.builder().errorId(0L).errorMessage(ex.getMessage()).build();
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }
}
