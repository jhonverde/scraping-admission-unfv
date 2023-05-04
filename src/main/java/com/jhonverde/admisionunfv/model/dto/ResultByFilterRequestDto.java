package com.jhonverde.admisionunfv.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultByFilterRequestDto {

    @NotNull(message = "searchStarValue is not valid")
    private Integer searchStarValue;

    private Integer searchFinishValue;

    @NotBlank(message = "speciality is not valid")
    private String speciality;

}
