package com.arms.api.report.fulldata.service;

import com.arms.api.jira.jiraproject_pure.model.JiraProjectPureEntity;
import com.arms.api.jira.jiraproject_pure.service.JiraProjectPure;
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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("FullDataService")
@RequiredArgsConstructor
public class FullDataServiceImpl implements FullDataService {

    private final PdService pdService;

    private final EngineService engineService;

    private final ReqAdd reqAdd;

    private final JiraProjectPure jiraProjectPure;
    @Override
    public List<FullDataAssigneesResponse> getAssignees(FullDataRequestDTO fullDataRequestDTO) {
        ResponseEntity<List<FullDataAssigneesResponse>> fullDataReportAssignees = engineService.getAssignees(fullDataRequestDTO);
        return fullDataReportAssignees.getBody();
    }

    @Override
    public List<지라이슈> getExcelData(String changeReqTableName, FullDataRequestDTO fullDataRequestDTO) throws Exception {
//        List<JiraProjectPureEntity> almProjects = jiraProjectPure.getJiraProjects(fullDataRequestDTO.getAlmProjectIds());
//        List<String> almProjectUrls = almProjects.stream().map(JiraProjectPureEntity::getC_jira_url).collect(Collectors.toList());
//        fullDataRequestDTO.setAlmProjectUrls(almProjectUrls);
        SessionUtil.setAttribute("excel-data", changeReqTableName);
        ResponseEntity<List<지라이슈>> 이슈조회 = engineService.getExcelData(fullDataRequestDTO);

        ReqAddEntity reqAddEntity = new ReqAddEntity();
        Map<Long, ReqAddEntity> reqAddEntityMap = reqAdd.getChildNodeWithoutPaging(reqAddEntity)
                .stream().collect(Collectors.toMap(ReqAddEntity::getC_id, a -> a));

        log.info("[ FullDataServiceImpl :: getExcelData ] :: getChildNodeWithoutPaging start");
        System.out.println(reqAddEntityMap);

        SessionUtil.removeAttribute("excel-data");

        return 이슈조회.getBody();
    }
}
