package com.jhonverde.admisionunfv.service;

import com.jhonverde.admisionunfv.config.AdmissionProperties;
import com.jhonverde.admisionunfv.exception.BadRequestException;
import com.jhonverde.admisionunfv.model.constant.SearchType;
import com.jhonverde.admisionunfv.model.constant.TagResult;
import com.jhonverde.admisionunfv.model.dto.ResultByFilterRequestDto;
import com.jhonverde.admisionunfv.model.dto.ResultByFilterResponseDto;
import com.jhonverde.admisionunfv.model.dto.ResultResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResultFacade {

    private final AdmissionProperties admissionProperties;

    private final RestTemplate restTemplate;

    private final ExcelHelper excelHelper;

    public ResultResponseDto getByTypeAndValue(String searchType, String searchValue) {
        var searchTypeEnum = SearchType.getBySearchName(searchType)
                .orElseThrow(() -> new BadRequestException("SearchType is not valid"));

        var contentHtml = getHtmlBySearchResult(searchTypeEnum, searchValue);

        return getResultDtoByHtml(contentHtml);
    }

    public ResultByFilterResponseDto getResultsByFilterResponseDto(ResultByFilterRequestDto requestDto) {
        List<ResultResponseDto> results = new ArrayList<>();
        Integer searchStartValue = requestDto.getSearchStarValue();
        Integer searchFinisValue = requestDto.getSearchFinishValue();
        boolean searchForRange = searchFinisValue != null;

        while(searchForRange ? searchStartValue <= searchFinisValue : true) {
            ResultResponseDto resultResponseDto = getByTypeAndValue(SearchType.CODE.name(), String.valueOf(searchStartValue));
            log.info("Search value: {}, ResultResponseDto: {}", searchStartValue, resultResponseDto);

            if(!searchForRange && TagResult.NO_EXISTEN_DATOS_QUE_MOSTRAR.getDescription().equals(resultResponseDto.getEstadoMensaje())) {
                break;
            }

            if(StringUtils.hasText(resultResponseDto.getModalidadYEspecialidad())
                    && resultResponseDto.getModalidadYEspecialidad().contains(requestDto.getSpeciality())) {
                results.add(resultResponseDto);
            }

            searchStartValue++;
        }

        List<ResultResponseDto> resultsFilter = results.stream()
                .filter(r -> TagResult.INGRESO.getDescription().equals(r.getCondicion()))
                .collect(Collectors.toList());

        Comparator<ResultResponseDto> orderByScoreBySurnames = Comparator.comparing(ResultResponseDto::getPuntaje).reversed()
                .thenComparing(ResultResponseDto::getApellidosYNombres);

        Collections.sort(results, orderByScoreBySurnames);

        AtomicInteger count = new AtomicInteger(1);

        results = results.stream().map(result -> {
            result.setRanking(count.get());
            count.getAndIncrement();
            return result;
        }).collect(Collectors.toList());

        return ResultByFilterResponseDto.builder()
                .carrera(requestDto.getSpeciality())
                .puntajeMinimoIngreso(resultsFilter.stream().mapToDouble(r -> r.getPuntaje()).min().orElse(0D))
                .puntajeMaximoIngreso(resultsFilter.stream().mapToDouble(r -> r.getPuntaje()).max().orElse(0D))
                .numeroIngresantes(resultsFilter.size())
                .numeroPostulantes(results.size())
                .resultados(results)
                .build();
    }

    public ResultByFilterResponseDto generateFileDownload(ResultByFilterResponseDto responseDto) throws IOException {
        String fileName = excelHelper.generateExcelByDataAndReturnName(responseDto);
        String urlDownloadFile = "http://localhost:8080/api/results/download/" + fileName;

        return responseDto.agregarUrlDescarga(urlDownloadFile);
    }

    private String getHtmlBySearchResult(SearchType searchType, String searchValue) {
        URI admissionUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("__VIEWSTATE", admissionProperties.getFormData().getViewsState());
        formData.add("__VIEWSTATEGENERATOR", admissionProperties.getFormData().getViewsStateGenerator());
        formData.add("__EVENTVALIDATION", admissionProperties.getFormData().getEventValidation());
        formData.add("CboTipo", String.valueOf(searchType.getSearchCode()));
        formData.add("Txt_Busqueda", searchValue);
        formData.add("Btn_Buscar", "Buscar");

        try {
            admissionUrl = new URI(admissionProperties.getUrl());
        } catch (URISyntaxException e) {
            throw new RuntimeException("There was an error when processing the admission url");
        }

        RequestEntity requestEntity = new RequestEntity(formData, headers, HttpMethod.POST, admissionUrl);
        ResponseEntity<String> response = restTemplate.postForEntity(admissionUrl, requestEntity, String.class);

        return response.getBody();
    }

    private ResultResponseDto getResultDtoByHtml(String html) {
        Document document = Jsoup.parse(html);
        Element elementResultado = document.getElementById("GrdVw_Resultado");

        if(elementResultado == null) {
            log.error("GrdVw_Resultado ID not found");
            return ResultResponseDto.responseError(TagResult.NO_EXISTEN_DATOS_QUE_MOSTRAR.getDescription());
        }

        Elements elementsTd = elementResultado.select("td");
        String textTagCondition = elementsTd.text();

        if(textTagCondition.contains(TagResult.NO_EXISTEN_DATOS_QUE_MOSTRAR.getDescription())) {
            return ResultResponseDto.responseError(TagResult.NO_EXISTEN_DATOS_QUE_MOSTRAR.getDescription());
        }

        var elementSurnamesAndNames = Optional.of(document.getElementById("Lbl_Nombre"));
        var elementModalityAndSpeciality = Optional.of(document.getElementById("Lbl_Especialidad"));
        var elementScore = Optional.of(document.getElementById("Lbl_Modalidad"));
        var surnamesAndNames = elementSurnamesAndNames.isEmpty() ? "" : elementSurnamesAndNames.get().text();
        var modalityAndSpeciality = elementModalityAndSpeciality.isEmpty() ? "" : elementModalityAndSpeciality.get().text();
        var score = elementScore.isEmpty() ? "" : elementScore.get().text();

        score = score.replace(",", ".");

        return ResultResponseDto.builder()
                .apellidosYNombres(surnamesAndNames)
                .modalidadYEspecialidad(modalityAndSpeciality)
                .puntaje(Double.parseDouble(score))
                .condicion(textTagCondition)
                .build();
    }
}
