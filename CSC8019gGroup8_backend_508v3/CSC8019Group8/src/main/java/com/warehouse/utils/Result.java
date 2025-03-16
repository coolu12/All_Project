package com.warehouse.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code; //The response code 1 represents success and 0 represents failure
    private String msg;   //Response message
    private Object data; //Returned data

    //Add delete modify successful response
    public static Result success() {
        return new Result(1, "success", null);
    }

    //Query successful response
    public static Result success(Object data) {
        return new Result(1, "success", data);
    }

    //Response failure
    public static Result error(String msg) {
        return new Result(0, msg, null);
    }

}
