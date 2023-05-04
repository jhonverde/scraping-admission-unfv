package com.jhonverde.admisionunfv.model.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagResult {

    NO_EXISTEN_DATOS_QUE_MOSTRAR("No existen datos que mostrar"),
    INGRESO("INGRESO"),
    NO_INGRESO("NO INGRESO"),
    NO_RESULTADO_POR_CODIGO("POR CODIGO"),
    NO_RESULTADO_SIN_TEMA("SIN TEMA"),
    AUSENTE("AUSENTE");

    private String description;

}
