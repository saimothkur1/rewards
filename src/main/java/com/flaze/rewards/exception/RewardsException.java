package com.flaze.rewards.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardsException extends Exception{

    private List<Error> errorList = new ArrayList<>();
    private HttpStatus errorCode;
}
