package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.dto.DocumentFormDTO;
import com.springcore.ai.scai_platform.dto.DocumentSearchReq;
import com.springcore.ai.scai_platform.dto.DocumentSearchResp;
import com.springcore.ai.scai_platform.dto.SupervisorInfo;
import com.springcore.ai.scai_platform.entity.Document;
import com.springcore.ai.scai_platform.entity.Employee;
import com.springcore.ai.scai_platform.entity.FlowDoc;
import com.springcore.ai.scai_platform.entity.FlowDocStep;
import com.springcore.ai.scai_platform.mapper.DocumentMapper;
import com.springcore.ai.scai_platform.repository.api.DocumentRepository;
import com.springcore.ai.scai_platform.repository.api.DocumentRepositoryCustom;
import com.springcore.ai.scai_platform.repository.api.EmployeeHierarchyRepository;
import com.springcore.ai.scai_platform.repository.api.EmployeeRepository;
import com.springcore.ai.scai_platform.repository.api.FlowDocRepository;
import com.springcore.ai.scai_platform.security.UserContext;
import com.springcore.ai.scai_platform.service.api.DocumentService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final FlowDocRepository flowDocRepository;
    private final EmployeeHierarchyRepository hierarchyRepository;
    private final DocumentMapper documentMapper;
    private static final String userName = UserContext.getUserName();

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository
            , DocumentRepositoryCustom documentRepositoryCustom
            , EmployeeRepository employeeRepository, FlowDocRepository flowDocRepository, EmployeeHierarchyRepository hierarchyRepository, DocumentMapper documentMapper
    ) {
        this.documentRepository = documentRepository;
        this.documentRepositoryCustom = documentRepositoryCustom;
        this.employeeRepository = employeeRepository;
        this.flowDocRepository = flowDocRepository;
        this.hierarchyRepository = hierarchyRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    public Document save(Document form) {
        log.debug("save {}", form);
        String documentNo = generateDocumentNo(form.getDocumentType());
        form.setDocumentNo(documentNo);
        form.setCreateBy(userName);
        form.getAttachment().forEach(attachment -> attachment.setCreateBy(userName));
        Document saved = documentRepository.save(form);

        log.debug("saved document id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public FlowDoc generateFlow(DocumentFormDTO formDTO) {
        int maxStep = 3; // Todo Get Max Step From Config

        FlowDoc flow = new FlowDoc();
        flow.setDocNo(formDTO.getDocumentNo());
        flow.setDocType(formDTO.getDocumentType());
        flow.setDocumentForm(formDTO);
        flow.setCreatedDate(LocalDateTime.now());
        flow.setRequestedDate(LocalDateTime.now());
        flow.setActiveStep(BigDecimal.ONE);
        flow.setLastStep(BigDecimal.valueOf(maxStep));
        flow.setInactive(BigDecimal.ZERO);

        Document form = documentMapper.toEntity(formDTO);
        List<SupervisorInfo> supervisors = hierarchyRepository.findSupervisors(form.getEmId());
        supervisors.forEach(sup -> log.info("Level {}: {} - {}", sup.getLevel(), sup.getEmCode(), sup.getEmName()));

        // แปลงเป็น Map เหมือนเดิม
        Map<Integer, SupervisorInfo> supervisorMap = supervisors.stream()
                .collect(Collectors.toMap(SupervisorInfo::getLevel, sup -> sup));

        List<FlowDocStep> steps = new ArrayList<>();
        if (supervisorMap.isEmpty()) {
            FlowDocStep step = new FlowDocStep();
            step.setFlowDoc(flow);
            step.setStepno(BigDecimal.ZERO);
            step.setActionType("Request");
            step.setEmman(form.getEmId());
            step.setEmmanInfo(Employee.builder()
                    .id(form.getEmId())
                    .build());
            step.setIsActive(1);
            step.setIsend(BigDecimal.ONE);
            step.setMailstat(BigDecimal.ZERO);
            step.setReqCancel(0);
            steps.add(step);
            flow.setSteps(steps);
            return flow;
        }



        FlowDocStep step = new FlowDocStep();
        // Requester
        {
            Employee employeeLogin = UserContext.getEmployee();
            Long emId = (employeeLogin != null) ? employeeLogin.getId() : null;
            step.setFlowDoc(flow);
            step.setStepno(BigDecimal.ZERO);
            step.setEmmanInfo(employeeLogin);
            step.setEmman(emId);
            step.setIsActive(1);
            step.setIsend(BigDecimal.ZERO);
            step.setMailstat(BigDecimal.ZERO);
            step.setReqCancel(0);
            steps.add(step);
        }

        // 2. ลูปสร้าง 3 Step
        for (int i = 1; i <= 3; i++) {
            step = new FlowDocStep();
            step.setFlowDoc(flow);
            step.setStepno(new BigDecimal(i));

            SupervisorInfo supInfo = supervisorMap.get(i);
            Long approverId;
            if (supInfo != null) {
                approverId = supInfo.getEmId();
                step.setEmmanInfo(Employee.builder()
                        .id(approverId)
                        .code(supInfo.getEmCode())
                        .name(supInfo.getEmName())
                        .build());
            } else if (!supervisors.isEmpty()) {
                SupervisorInfo findSup = supervisors.get(supervisors.size() - 1);
                approverId = findSup.getEmId();
                step.setEmmanInfo(Employee.builder()
                        .id(approverId)
                        .code(findSup.getEmCode())
                        .name(findSup.getEmName())
                        .build());
            } else {
                approverId = form.getEmId();
                step.setActionType("Request");
            }

            step.setEmman(approverId);
            step.setIsActive(0);
            step.setIsend(i == 3 ? BigDecimal.ONE : BigDecimal.ZERO);
            step.setMailstat(BigDecimal.ZERO);
            step.setReqCancel(0);
            steps.add(step);
        }

        flow.setSteps(steps);
        return flow;
    }

    @Override
    @Transactional
    public FlowDoc submitFlow(FlowDoc flowDoc) {
        DocumentFormDTO documentFormDTO = flowDoc.getDocumentForm();
        Document document = documentMapper.toEntity(documentFormDTO);
        document = save(document);

        flowDoc.setDocId(document.getId());
        flowDoc.setDocNo(document.getDocumentNo());
        flowDoc.setCreatedDate(LocalDateTime.now());

        if (flowDoc.getSteps() != null) {
            flowDoc.getSteps().forEach(step -> step.setFlowDoc(flowDoc));
        }

        return flowDocRepository.save(flowDoc);
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

    @Transactional
    public boolean deleteById(Long id) {
        if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id);
            return true;
        }
        return false;
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
