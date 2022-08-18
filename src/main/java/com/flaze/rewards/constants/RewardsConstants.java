package com.flaze.rewards.constants;

import lombok.Data;

@Data
public class RewardsConstants {

    //Controller Constants
    public static final String API = "/api";
    public static final String CREATE_TRANSACTION_PATH = "/createtransaction";
    public static final String REWARDS_BY_DATE_PATH = "/rewards/{customer-id}";
    public static final String CUSTOMER_ID_PARAM = "customer-id";
    public static final String AMOUNT_PARAM = "amount";
    public static final String DATE_AFTER_PARAM = "date-after";
    public static final String INPUT_DATE_FORMAT = "yyyy-MM-dd";


    //Rewards Service Constants
    public static final Integer DEFAULT_MONTHS_TO_SUBTRACT = 3;

    //Error Constants
    public static final String DB_ERROR_MESSAGE = "Db Exception Occurred when trying to fetch customer transaction details";
    public static final String DB_ERROR_INSERT_MESSAGE = "Db Exception Occurred when trying to persist customer transaction details";

    public static final String TRANSACTION_NOT_FOUND_ERROR_MESSAGE = "Database Error";
    public static final String TRANSACTION_NOT_FOUND_ERROR_DETAIL = "Transactions doesn't exist for the combination ";

}
