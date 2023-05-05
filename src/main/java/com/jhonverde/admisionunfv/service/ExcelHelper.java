package com.jhonverde.admisionunfv.service;

import com.jhonverde.admisionunfv.model.dto.ResultByFilterResponseDto;
import com.jhonverde.admisionunfv.model.dto.ResultResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class ExcelHelper {

    public String generateExcelByDataAndReturnName(ResultByFilterResponseDto responseDto) throws IOException {
        String nombreYExtensionReporteExcel = responseDto.getCarrera() + ".xlsx";
        File fileReporteExcelGenerado = new File("results" + File.separator + nombreYExtensionReporteExcel);

        SXSSFWorkbook workbook = new SXSSFWorkbook();

        SXSSFSheet sheet = workbook.createSheet("Resultados");

        sheet.trackAllColumnsForAutoSizing();

        CellStyle headerStyle = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        SXSSFRow header = sheet.createRow(0);

        SXSSFCell headerCell = header.createCell(0);
        headerCell.setCellValue("Carrera");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Puntaje Mínimo Ingreso");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Puntaje Máximo Ingreso");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Número de ingresantes");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Número de postulantes");
        headerCell.setCellStyle(headerStyle);

        SXSSFRow row = sheet.createRow(1);
        Cell cell = row.createCell(0);
        cell.setCellValue(responseDto.getCarrera());

        cell = row.createCell(1);
        cell.setCellValue(responseDto.getPuntajeMinimoIngreso());

        cell = row.createCell(2);
        cell.setCellValue(responseDto.getPuntajeMaximoIngreso());

        cell = row.createCell(3);
        cell.setCellValue(responseDto.getNumeroIngresantes());

        cell = row.createCell(4);
        cell.setCellValue(responseDto.getNumeroPostulantes());

        sheet.createRow(2);

        row = sheet.createRow(3);

        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 4));

        cell = row.createCell(0);
        cell.setCellValue("Resultados");
        cell.setCellStyle(headerStyle);

        row = sheet.createRow(4);

        cell = row.createCell(0);
        cell.setCellValue("Ranking");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(1);
        cell.setCellValue("Apellidos y Nombres");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(2);
        cell.setCellValue("Modalidad y Especialidad");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(3);
        cell.setCellValue("Puntaje");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(4);
        cell.setCellValue("Condición");
        cell.setCellStyle(headerStyle);

        List<ResultResponseDto> results = responseDto.getResultados();
        int rowCount = 5;
        int columnCount;

        for (ResultResponseDto result : results) {
            columnCount = 0;
            row = sheet.createRow(rowCount++);

            cell = row.createCell(columnCount++);
            cell.setCellValue(result.getRanking());

            cell = row.createCell(columnCount++);
            cell.setCellValue(result.getApellidosYNombres());

            cell = row.createCell(columnCount++);
            cell.setCellValue(result.getModalidadYEspecialidad());

            cell = row.createCell(columnCount++);
            cell.setCellValue(result.getPuntaje());

            cell = row.createCell(columnCount);
            cell.setCellValue(result.getCondicion());
        }

        for (int column = 0; column < 5; column++) {
            sheet.autoSizeColumn(column);
        }

        var fileOutputStream = new FileOutputStream(fileReporteExcelGenerado.getAbsolutePath());

        workbook.write(fileOutputStream);

        return nombreYExtensionReporteExcel;
    }

}
