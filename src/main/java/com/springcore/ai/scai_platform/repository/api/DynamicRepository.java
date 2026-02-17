package com.springcore.ai.scai_platform.repository.api;

import java.util.List;

public interface DynamicRepository {
    List<?> fetchData(String clazzLookup);
}