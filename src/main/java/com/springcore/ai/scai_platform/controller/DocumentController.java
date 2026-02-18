package com.springcore.ai.scai_platform.controller;

import com.springcore.ai.scai_platform.dto.DocumentSearchReq;
import com.springcore.ai.scai_platform.dto.DocumentSearchResp;
import com.springcore.ai.scai_platform.entity.Document;
import com.springcore.ai.scai_platform.service.api.DocumentService;
import com.springcore.ai.scai_platform.service.api.FileService;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/document")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/submit")
    public ResponseEntity<Object> submit(@RequestBody Document from) {
        try {
            return ResponseEntity.ok(documentService.submit(from));
        } catch (ValidationException ex) {
            log.error("ValidationException ::", ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception ex) {

            log.error("Exception", ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<DocumentSearchResp>> search(@RequestBody DocumentSearchReq criteria) {
        return ResponseEntity.ok(documentService.search(criteria));
    }
}
