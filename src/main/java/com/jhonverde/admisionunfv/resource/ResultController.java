package com.jhonverde.admisionunfv.resource;

import com.jhonverde.admisionunfv.exception.NotFoundException;
import com.jhonverde.admisionunfv.model.dto.ResultByFilterRequestDto;
import com.jhonverde.admisionunfv.model.dto.ResultByFilterResponseDto;
import com.jhonverde.admisionunfv.model.dto.ResultResponseDto;
import com.jhonverde.admisionunfv.service.ResultFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultFacade resultFacade;

    @GetMapping
    public ResultResponseDto getResultByTypeAndValue(@RequestParam String searchType, @RequestParam String searchValue) {
        return resultFacade.getByTypeAndValue(searchType, searchValue);
    }

    @PostMapping
    public ResultByFilterResponseDto getResultsByFilterSpeciality(@Valid @RequestBody ResultByFilterRequestDto resultByFilterRequestDto) throws IOException {
        ResultByFilterResponseDto resultByFilterResponseDto = resultFacade.getResultsByFilterResponseDto(resultByFilterRequestDto);
        return resultFacade.generateFileDownload(resultByFilterResponseDto);
    }

    @GetMapping("/download/{nameFile}")
    public ResponseEntity<?> downloadGenerateResultsFile(@PathVariable(name = "nameFile") String nameFile) throws IOException {
        File generatedFile = new File("results" + File.separator + nameFile);

        if(!generatedFile.exists()) {
            throw new NotFoundException("File not found");
        }

        Path path = Paths.get(generatedFile.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        String headerAttachment = "attachment; filename=\"" + nameFile + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, headerAttachment)
                .contentLength(generatedFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
