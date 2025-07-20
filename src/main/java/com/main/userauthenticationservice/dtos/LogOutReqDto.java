package com.main.userauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogOutReqDto {
    private String token;
    private Long userId;
}
