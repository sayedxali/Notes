package com.seyed.ali.task2securityauditing.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    private boolean flag; // Two values: true means success, false means not successful
    private Integer code; // Status code. e.g., 200
    private String message; // Response message
    private Object data; // The response payload

    public Result(boolean flag, Integer code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

}
