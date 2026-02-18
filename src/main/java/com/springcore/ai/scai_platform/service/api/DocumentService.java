package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.dto.DocumentSearchReq;
import com.springcore.ai.scai_platform.dto.DocumentSearchResp;
import com.springcore.ai.scai_platform.entity.Document;

import java.util.List;

public interface DocumentService {
    Document submit(Document form);

    List<DocumentSearchResp> search(DocumentSearchReq form);

}
