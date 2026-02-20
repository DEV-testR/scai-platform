package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.dto.DocumentFormDTO;
import com.springcore.ai.scai_platform.dto.DocumentSearchReq;
import com.springcore.ai.scai_platform.dto.DocumentSearchResp;
import com.springcore.ai.scai_platform.entity.Document;
import com.springcore.ai.scai_platform.entity.FlowDoc;

import java.util.List;

public interface DocumentService {
    Document save(Document form);

    FlowDoc generateFlow(DocumentFormDTO form);

    FlowDoc submitFlow(FlowDoc flowDoc);

    List<DocumentSearchResp> search(DocumentSearchReq form);

    boolean deleteById(Long id);

}
