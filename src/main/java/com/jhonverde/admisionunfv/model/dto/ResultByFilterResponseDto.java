package com.jhonverde.admisionunfv.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ResultByFilterResponseDto {

    private String carrera;

    private Double puntajeMinimoIngreso;

    private Double puntajeMaximoIngreso;

    private Integer numeroIngresantes;

    private Integer numeroPostulantes;

    private List<ResultResponseDto> resultados;

    private String urlDescarga;

    public ResultByFilterResponseDto agregarUrlDescarga(String urlDescarga) {
        this.urlDescarga = urlDescarga;
        return this;
    }

}
