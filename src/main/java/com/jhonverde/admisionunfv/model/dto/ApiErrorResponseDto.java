package com.jhonverde.admisionunfv.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ApiErrorResponseDto {

    private String message;

}
