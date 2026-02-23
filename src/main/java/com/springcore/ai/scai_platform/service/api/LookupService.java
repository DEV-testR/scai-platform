package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.dto.LookupItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LookupService {
    List<LookupItem> getDynamicLookup(String clazzLookup);

    Object getValueByReflection(Object object, String fldName);
}


