package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.dto.DocumentSearchReq;
import com.springcore.ai.scai_platform.dto.DocumentSearchResp;
import com.springcore.ai.scai_platform.entity.Document;
import com.springcore.ai.scai_platform.entity.Employee;
import com.springcore.ai.scai_platform.repository.api.DocumentRepository;
import com.springcore.ai.scai_platform.repository.api.DocumentRepositoryCustom;
import com.springcore.ai.scai_platform.repository.api.EmployeeRepository;
import com.springcore.ai.scai_platform.service.api.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentRepositoryCustom documentRepositoryCustom;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository
            , DocumentRepositoryCustom documentRepositoryCustom
            , EmployeeRepository employeeRepository
    ) {
        this.documentRepository = documentRepository;
        this.documentRepositoryCustom = documentRepositoryCustom;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Document submit(Document form) {
        log.debug("submit {}", form);
        String documentNo = generateDocumentNo(form.getDocumentType());
        form.setDocumentNo(documentNo);
        form.setCreateBy("APP_TES");
        form.getAttachment().forEach(attachment -> attachment.setCreateBy("APP_TES"));
        Document saved = documentRepository.save(form);

        log.debug("saved document id={}", saved.getId());
        return saved;
    }

    @Override
    public List<DocumentSearchResp> search(DocumentSearchReq criteria) {
        List<Document> documents = documentRepositoryCustom.searchByCriteria(criteria);
        Set<Long> emIds = documents.stream()
                .map(Document::getEmId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Employee> employeeMap = employeeRepository.findAllById(emIds).stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));

        return documents.parallelStream().map(r -> {
                    Employee employee = (r.getEmId() != null) ? employeeMap.get(r.getEmId()) : null;
                    return DocumentSearchResp
                            .builder()
                            .id(r.getId())
                            .emId(employee)
                            .documentNo(r.getDocumentNo())
                            .documentType(r.getDocumentType())
                            .documentStatus(r.getDocumentStatus())
                            .documentDate(r.getDateWork())
                            .build();
                })
                .toList();
    }

    private String generateDocumentNo(String documentType) {
        String format = java.time.YearMonth.now().toString().replace("-", "");
        List<Document> latestList = documentRepository.findTopByTypeAndMonthOrderByIdDesc(documentType, format);

        int running = 1;
        if (!latestList.isEmpty()) {
            String lastNo = latestList.get(0).getDocumentNo();
            String lastRunning = lastNo.substring(lastNo.lastIndexOf("-") + 1);
            running = Integer.parseInt(lastRunning) + 1;
        }

        return String.format("%s-%s-%04d", documentType, format, running);
    }

}
