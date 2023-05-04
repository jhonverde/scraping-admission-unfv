package com.jhonverde.admisionunfv.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class ResultResponseDto {

    private Integer ranking;

    private String apellidosYNombres;

    private String modalidadYEspecialidad;

    private Double puntaje;

    private String condicion;

    @Builder.Default
    @JsonIgnore
    private String estadoMensaje = "OK";

    public static ResultResponseDto responseError(String statusMessage) {
        return ResultResponseDto.builder().estadoMensaje(statusMessage).build();
    }
}
