package com.springcore.ai.scai_platform.controller;

import com.springcore.ai.scai_platform.dto.LookupResponse;
import com.springcore.ai.scai_platform.service.api.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lookup")
@RequiredArgsConstructor
public class LookupController {
    private final LookupService lookupService;

    @GetMapping("fetchData/{clazzLookup}")
    @ResponseBody
    public ResponseEntity<List<LookupResponse>> fetchData(@PathVariable String clazzLookup) {
        return ResponseEntity.ok(lookupService.getDynamicLookup(clazzLookup));
    }

}

