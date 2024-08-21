package com.arms.api.report.fulldata.service;

import com.arms.api.product_service.pdservice.model.PdServiceEntity;
import com.arms.api.product_service.pdservice.service.PdService;
import com.arms.api.report.fulldata.model.FullDataAssigneesResponse;
import com.arms.api.report.fulldata.model.FullDataRequestDTO;
import com.arms.api.requirement.reqadd.model.ReqAddEntity;
import com.arms.api.requirement.reqadd.service.ReqAdd;
import com.arms.api.util.communicate.external.EngineService;
import com.arms.api.util.communicate.external.response.jira.지라이슈;
import com.arms.egovframework.javaservice.treeframework.interceptor.SessionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("FullDataService")
@RequiredArgsConstructor
public class FullDataServiceImpl implements FullDataService {

    private final PdService PdService;
    // private PdService PdService;

    private final EngineService engineService;

    private final ReqAdd reqAdd;

    @Override
    public List<FullDataAssigneesResponse> getAssignees(FullDataRequestDTO fullDataRequestDTO) {
        ResponseEntity<List<FullDataAssigneesResponse>> fullDataReportAssignees = engineService.getAssignees(fullDataRequestDTO);
        return fullDataReportAssignees.getBody();
    }

    @Override
    public List<지라이슈> getExcelData(FullDataRequestDTO fullDataRequestDTO) throws Exception {
        SessionUtil.setAttribute("excel-data", "T_ARMS_REQADD_25");
        ResponseEntity<List<지라이슈>> 이슈조회 = engineService.getExcelData(fullDataRequestDTO);
        List<ReqAddEntity> nodesWithoutRoot1 = reqAdd.getChildNode(new ReqAddEntity());
        System.out.println(nodesWithoutRoot1);

        List<PdServiceEntity> nodesWithoutRoot = PdService.getNodesWithoutRoot(new PdServiceEntity());
        System.out.println(nodesWithoutRoot);

        SessionUtil.removeAttribute("excel-data");
        return 이슈조회.getBody();
    }
}
