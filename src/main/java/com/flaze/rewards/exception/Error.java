package com.flaze.rewards.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Error {

    private String errorMessage;
    private String status;
    private String errorDetail;
}
