package com.springcore.ai.scai_platform.controller;

import com.springcore.ai.scai_platform.dto.DocumentFormDTO;
import com.springcore.ai.scai_platform.dto.DocumentSearchReq;
import com.springcore.ai.scai_platform.dto.DocumentSearchResp;
import com.springcore.ai.scai_platform.entity.Document;
import com.springcore.ai.scai_platform.service.api.DocumentService;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
    @PostMapping("/save")
    public ResponseEntity<Object> save(@RequestBody Document from) {
        try {
            return ResponseEntity.ok(documentService.save(from));
        } catch (ValidationException ex) {
            log.error("ValidationException ::", ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception ex) {

            log.error("Exception", ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/generate-flow")
    @ResponseBody
    public ResponseEntity<Object> generateFlow(@RequestBody DocumentFormDTO from) {
        try {
            return ResponseEntity.ok(documentService.generateFlow(from));
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            boolean isDeleted = documentService.deleteById(id);
            if (isDeleted) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", "Deleted successfully");
                return ResponseEntity.ok().body(map);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
