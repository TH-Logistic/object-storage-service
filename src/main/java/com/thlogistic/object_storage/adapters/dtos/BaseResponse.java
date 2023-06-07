package com.thlogistic.object_storage.adapters.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {
    String message;
    Boolean success;
    T data;
}
