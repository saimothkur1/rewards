package com.flaze.rewards.controller.advice;

import com.flaze.rewards.controller.RewardsCalculatorController;
import com.flaze.rewards.exception.Error;
import com.flaze.rewards.exception.RewardsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import java.util.List;

import static com.flaze.rewards.constants.RewardsConstants.INPUT_DATE_FORMAT;

@ControllerAdvice(assignableTypes = RewardsCalculatorController.class)
public class RewardsCalculatorControllerAdvice {

    //Handle RewardsException
    @ExceptionHandler(RewardsException.class)
    public ResponseEntity<List<Error>> handleException(RewardsException exception, HttpServletRequest httpServletRequest){
        return new ResponseEntity<>(exception.getErrorList(), exception.getErrorCode());
    }

    //Handle ConstraintViolationException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleContraintViolationException(ConstraintViolationException exception) {
        return new ResponseEntity<>( "Invalid input: " + exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    //Handle MethodArgumentTypeMismatchException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return new ResponseEntity<>( "Invalid input: Date must be in the format " + INPUT_DATE_FORMAT, HttpStatus.BAD_REQUEST);
    }
}
