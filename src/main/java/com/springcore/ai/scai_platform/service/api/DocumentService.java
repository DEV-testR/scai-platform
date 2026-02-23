package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.dto.DocumentSearchReq;
import com.springcore.ai.scai_platform.dto.DocumentSearchResp;
import com.springcore.ai.scai_platform.entity.Document;

import java.util.List;

public interface DocumentService {
    Document save(Document form);

    Document generateFlow(Document doc);

    Document submitFlow(Document doc);

    List<Document> search(DocumentSearchReq form);

    Document searchById(Long id);

    boolean deleteById(Long id);

}
