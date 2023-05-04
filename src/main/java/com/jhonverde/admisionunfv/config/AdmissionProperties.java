package com.jhonverde.admisionunfv.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "admission")
@Getter
@Setter
public class AdmissionProperties {

    private String url;

    private FormData formData;

    @Getter
    @Setter
    public static class FormData {

        private String viewsState;

        private String viewsStateGenerator;

        private String eventValidation;

    }
}
