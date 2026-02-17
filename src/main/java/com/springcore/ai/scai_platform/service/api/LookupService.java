package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.dto.LookupResponse;
import com.springcore.ai.scai_platform.dto.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LookupService {
    List<LookupResponse> getDynamicLookup(String clazzLookup);

    Object getValueByReflection(Object object, String fldName);
}


