package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.dto.LookupResponse;
import com.springcore.ai.scai_platform.repository.api.DynamicRepository;
import com.springcore.ai.scai_platform.service.api.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LookupServiceImpl implements LookupService {

    private final DynamicRepository dynamicRepository;

    @Autowired
    public LookupServiceImpl(DynamicRepository dynamicRepository) {
        this.dynamicRepository = dynamicRepository;
    }

    @Override
    public List<LookupResponse> getDynamicLookup(String clazzName) {
        return dynamicRepository.fetchData(clazzName)
                .stream()
                .map(r -> LookupResponse.builder()
                        .id((Long) getValueByReflection(r, "id"))
                        .code((String) getValueByReflection(r, "code"))
                        .name((String) getValueByReflection(r, "name"))
                        .build())
                .toList();
    }

    @Override
    public Object getValueByReflection(Object object, String fldName) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
