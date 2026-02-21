package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.domain.constant.DocumentStatus;
import com.springcore.ai.scai_platform.dto.DocumentFormDTO;
import com.springcore.ai.scai_platform.dto.DocumentSearchReq;
import com.springcore.ai.scai_platform.dto.DocumentSearchResp;
import com.springcore.ai.scai_platform.dto.FlowDocDTO;
import com.springcore.ai.scai_platform.dto.SupervisorInfo;
import com.springcore.ai.scai_platform.entity.Document;
import com.springcore.ai.scai_platform.entity.Employee;
import com.springcore.ai.scai_platform.entity.FlowDoc;
import com.springcore.ai.scai_platform.entity.FlowDocStep;
import com.springcore.ai.scai_platform.mapper.DocumentMapper;
import com.springcore.ai.scai_platform.mapper.FlowDocMapper;
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
    private final FlowDocMapper flowDocMapper;
    private static final String userName = UserContext.getUserName();

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository
            , DocumentRepositoryCustom documentRepositoryCustom
            , EmployeeRepository employeeRepository, FlowDocRepository flowDocRepository, EmployeeHierarchyRepository hierarchyRepository, DocumentMapper documentMapper, FlowDocMapper flowDocMapper
    ) {
        this.documentRepository = documentRepository;
        this.documentRepositoryCustom = documentRepositoryCustom;
        this.employeeRepository = employeeRepository;
        this.flowDocRepository = flowDocRepository;
        this.hierarchyRepository = hierarchyRepository;
        this.documentMapper = documentMapper;
        this.flowDocMapper = flowDocMapper;
    }

    @Override
    public DocumentFormDTO save(DocumentFormDTO doc) {
        log.debug("save {}", doc);
        Document entity = documentMapper.toEntity(doc);

        String documentNo = generateDocumentNo(doc.getDocumentType());
        entity.setDocumentNo(documentNo);
        entity.setCreateBy(userName);
        entity.getAttachment().forEach(attachment -> attachment.setCreateBy(userName));
        entity = documentRepository.save(entity);

        log.debug("saved document id={}", entity.getId());
        return documentMapper.toDto(entity);
    }

    @Override
    @Transactional
    public DocumentFormDTO generateFlow(DocumentFormDTO doc) {
        int maxStep = 3; // Todo Get Max Step From Config

        FlowDoc flow = new FlowDoc();
        flow.setDocNo(doc.getDocumentNo());
        flow.setDocType(doc.getDocumentType());
        flow.setCreatedDate(LocalDateTime.now());
        flow.setRequestedDate(LocalDateTime.now());
        flow.setActiveStep(0);
        flow.setLastStep(BigDecimal.valueOf(maxStep));
        flow.setInactive(BigDecimal.ZERO);

        Document form = documentMapper.toEntity(doc);
        List<SupervisorInfo> supervisors = hierarchyRepository.findSupervisors(form.getEmId());
        supervisors.forEach(sup -> log.info("Level {}: {} - {}", sup.getLevel(), sup.getEmCode(), sup.getEmName()));

        Map<Integer, SupervisorInfo> supervisorMap = supervisors.stream()
                .collect(Collectors.toMap(SupervisorInfo::getLevel, sup -> sup));

        List<FlowDocStep> steps = new ArrayList<>();
        if (supervisorMap.isEmpty()) {
            FlowDocStep step = new FlowDocStep();
            step.setFlowDoc(flow);
            step.setStepno(0);
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

            doc.setFlowDoc(flowDocMapper.toDto(flow));
            return doc;
        }

        FlowDocStep step = new FlowDocStep();

        // Requester
        Employee employeeLogin = UserContext.getEmployee();
        Long emId = (employeeLogin != null) ? employeeLogin.getId() : null;
        step.setFlowDoc(flow);
        step.setStepno(0);
        step.setEmmanInfo(employeeLogin);
        step.setEmman(emId);
        step.setIsActive(1);
        step.setIsend(BigDecimal.ZERO);
        step.setMailstat(BigDecimal.ZERO);
        step.setReqCancel(0);
        step.setActionType("REQUEST");
        step.setActionDate(LocalDateTime.now());
        steps.add(step);

        // 2. ลูปสร้าง 3 Step
        for (int i = 1; i <= 3; i++) {
            step = new FlowDocStep();
            step.setFlowDoc(flow);
            step.setStepno(i);

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
            }

            step.setActionType("WAITING");
            step.setEmman(approverId);
            step.setIsActive(0);
            step.setIsend(i == 3 ? BigDecimal.ONE : BigDecimal.ZERO);
            step.setMailstat(BigDecimal.ZERO);
            step.setReqCancel(0);
            steps.add(step);
        }

        flow.setSteps(steps);
        doc.setFlowDoc(flowDocMapper.toDto(flow));
        doc.setDocumentStatus(DocumentStatus.DRAFT);
        return doc;
    }

    @Override
    @Transactional
    public DocumentFormDTO submitFlow(DocumentFormDTO doc) {
        FlowDoc flowDoc = flowDocMapper.toEntity(doc.getFlowDoc());

        doc.setDocumentStatus(DocumentStatus.WAITING);
        Document document = documentMapper.toEntity(save(doc));

        flowDoc.setDocId(document.getId());
        flowDoc.setDocNo(document.getDocumentNo());
        flowDoc.setCreatedDate(LocalDateTime.now());
        if (flowDoc.getSteps() != null) {
            // หา Step ปัจจุบัน (ปกติคือ Step 0 ในตอนเริ่ม Submit)
            FlowDocStep currentStep = flowDoc.getSteps().stream()
                    .filter(s -> s.getStepno() == 0)
                    .findFirst()
                    .orElse(null);

            if (currentStep != null) {
                currentStep.setIsActive(0); // 1. ปิด Step ปัจจุบัน

                // 2. หา Next Step (Step ที่ 1)
                int nextStepNo = currentStep.getStepno() + 1;
                flowDoc.getSteps().stream()
                        .filter(s -> s.getStepno() == nextStepNo)
                        .findFirst()
                        .ifPresent(nextStep -> {
                            nextStep.setIsActive(1); // 2. เปิด Step ถัดไป
                            flowDoc.setActiveStep(nextStep.getStepno()); // 3. อัปเดตสถานะใน FlowDoc
                        });
            }

            flowDoc.getSteps().forEach(step -> step.setFlowDoc(flowDoc));
        }

        DocumentFormDTO docuemntDto = documentMapper.toDto(document);
        FlowDocDTO flowDocDTO = flowDocMapper.toDto(flowDocRepository.save(flowDoc));
        docuemntDto.setFlowDoc(flowDocDTO);
        return docuemntDto;
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

    @Override
    public DocumentFormDTO searchById(Long id) {
        Document document = documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Document Not Found"));
        Long docId = document.getId();
        DocumentFormDTO dto = documentMapper.toDto(document);
        flowDocRepository.findByDocId(docId).ifPresent(flowDoc -> {
            List<FlowDocStep> steps = flowDoc.getSteps();

            Set<Long> emIds = steps.stream().map(FlowDocStep::getEmman).filter(Objects::nonNull).collect(Collectors.toSet());
            Map<Long, Employee> employeeMap = employeeRepository.findAllById(emIds).stream()
                    .collect(Collectors.toMap(Employee::getId, e -> e));

            steps.forEach(step -> {
                Employee em = employeeMap.get(step.getEmman());
                step.setEmmanInfo(employeeMap.get(step.getEmman()));
            });

            dto.setFlowDoc(flowDocMapper.toDto(flowDoc));
        });
        return dto;
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!documentRepository.existsById(id)) {
            return false;
        }

        documentRepository.deleteById(id);
        if (flowDocRepository.existsByDocId(id)) {
            flowDocRepository.deleteByDocId(id);
        }
        return true;
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
