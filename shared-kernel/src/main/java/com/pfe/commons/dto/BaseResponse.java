package com.pfe.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(true, "Success", data);
    }

    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<T>(true, message, data);
    }

    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<T>(false, message, null);
    }
}
