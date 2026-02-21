package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.dto.DocumentFormDTO;
import com.springcore.ai.scai_platform.dto.DocumentSearchReq;
import com.springcore.ai.scai_platform.dto.DocumentSearchResp;
import com.springcore.ai.scai_platform.dto.FlowDocDTO;
import com.springcore.ai.scai_platform.entity.Document;
import com.springcore.ai.scai_platform.entity.FlowDoc;

import java.util.List;

public interface DocumentService {
    DocumentFormDTO save(DocumentFormDTO form);

    DocumentFormDTO generateFlow(DocumentFormDTO doc);

    DocumentFormDTO submitFlow(DocumentFormDTO doc);

    List<DocumentSearchResp> search(DocumentSearchReq form);

    DocumentFormDTO searchById(Long id);

    boolean deleteById(Long id);

}
