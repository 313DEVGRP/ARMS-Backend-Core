package com.arms.api.report.fulldata.service;

import com.arms.api.jira.jiraproject_pure.model.JiraProjectPureEntity;
import com.arms.api.jira.jiraproject_pure.service.JiraProjectPure;
import com.arms.api.product_service.pdservice.model.PdServiceEntity;
import com.arms.api.product_service.pdservice.service.PdService;
import com.arms.api.product_service.pdserviceversion.model.PdServiceVersionEntity;
import com.arms.api.report.fulldata.model.ExcelDataDTO;
import com.arms.api.report.fulldata.model.FullDataAssigneesResponse;
import com.arms.api.report.fulldata.model.FullDataRequestDTO;
import com.arms.api.report.fulldata.model.FullDataResponseDTO;
import com.arms.api.requirement.reqadd.service.ReqAdd;
import com.arms.api.requirement.reqadd_pure.model.ReqAddPureEntity;
import com.arms.api.requirement.reqadd_pure.service.ReqAddPure;
import com.arms.api.requirement.reqstate.model.ReqStateEntity;
import com.arms.api.requirement.reqstate.service.ReqState;
import com.arms.api.util.communicate.external.EngineService;
import com.arms.api.util.communicate.external.response.jira.지라이슈;
import com.arms.egovframework.javaservice.treeframework.interceptor.SessionUtil;

import com.arms.egovframework.javaservice.treeframework.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("FullDataService")
@RequiredArgsConstructor
public class FullDataServiceImpl implements FullDataService {

    private final PdService pdService;

    private final ReqState reqState;

    private final EngineService engineService;

    private final ReqAdd reqAdd;
    private final ReqAddPure reqAddPure;

    private final JiraProjectPure jiraProjectPure;
    @Override
    public List<FullDataAssigneesResponse> getAssignees(FullDataRequestDTO fullDataRequestDTO) throws Exception {
        List<JiraProjectPureEntity> almProjects = jiraProjectPure.getJiraProjects(fullDataRequestDTO.getAlmProjectIds());
        List<String> almProjectUrls = almProjects.stream().map(JiraProjectPureEntity::getC_jira_url).collect(Collectors.toList());
        fullDataRequestDTO.setAlmProjectUrls(almProjectUrls);
        ResponseEntity<List<FullDataAssigneesResponse>> fullDataReportAssignees = engineService.getAssignees(fullDataRequestDTO);
        return fullDataReportAssignees.getBody();
    }

    @Override
    public List<ExcelDataDTO> getExcelData(String changeReqTableName, FullDataRequestDTO fullDataRequestDTO) throws Exception {
        List<Long> almProjectList = Optional.ofNullable(fullDataRequestDTO.getAlmProjectIds()).orElse(Collections.emptyList());
        if(!almProjectList.isEmpty()) {
            List<JiraProjectPureEntity> almProjects = jiraProjectPure.getJiraProjects(fullDataRequestDTO.getAlmProjectIds());
            List<String> almProjectUrls = almProjects.stream().map(JiraProjectPureEntity::getC_jira_url).collect(Collectors.toList());
            fullDataRequestDTO.setAlmProjectUrls(almProjectUrls);
        }

        // 반환할 엑셀 데이터 리스트
        List<ExcelDataDTO> excelDataList = new ArrayList<>();

        SessionUtil.setAttribute("excel-data", changeReqTableName);
        ResponseEntity<FullDataResponseDTO> excelDataFromEngine = engineService.getExcelData(fullDataRequestDTO);


        ReqStateEntity reqStateEntity = new ReqStateEntity();
        List<ReqStateEntity> reqStateEntityList = reqState.getChildNodeWithoutPaging(reqStateEntity);
        Map<Long, String> 요구사항_상태_맵 = reqStateEntityList.stream().collect(Collectors.toMap(ReqStateEntity::getC_id, ReqStateEntity::getC_title));

        ReqAddPureEntity reqAddPureEntity = new ReqAddPureEntity();
        Map<Long, ReqAddPureEntity> reqAddPureEntityMap = reqAddPure.getChildNodeWithoutPaging(reqAddPureEntity)
                .stream().collect(Collectors.toMap(ReqAddPureEntity::getC_id, a -> a));

        String pdServiceStr = StringUtils.replace(changeReqTableName, "T_ARMS_REQADD_", "");

        // 제품서비스 관련
        PdServiceEntity 제품검색 = new PdServiceEntity();
        제품검색.setC_id(Long.valueOf(pdServiceStr));
        PdServiceEntity 제품검색_결과 = pdService.getNode(제품검색);
        String 제품명 = 제품검색_결과.getC_title();

        // 버전 관련
        Set<PdServiceVersionEntity> pdServiceVersionEntities = 제품검색_결과.getPdServiceVersionEntities();
        Map<Long, String> 버전아이디_이름및일정_맵 = 버전아이디_이름및일정_맵_구성(pdServiceVersionEntities);

        // ReqAdd
        List<지라이슈> ALM_이슈목록 = excelDataFromEngine.getBody().getIssueEntityList();
        if(ALM_이슈목록.isEmpty()) {
            log.info("[ FullDataServiceImpl :: getExcelData ] :: issue list is empty => 0");
            return excelDataList;
        }

        // isReq false 데이터 리스트
        List<ExcelDataDTO> subDataList = new ArrayList<>();
        // isReq true - key 및 idx Map
        Map<String, Integer> reqKeyIndexMap = new HashMap<>();

        for (지라이슈 issue : ALM_이슈목록) {
            ReqAddPureEntity reqAddEntityValue = reqAddPureEntityMap.get(issue.getCReqLink());

            if (reqAddEntityValue == null) { continue; }

            // 버전명 :: 버전A (시작일 ~ 종료일), 버전B (시작일 ~ 종료일), ...
            Long[] pdServiceVersions = issue.getPdServiceVersions();

            String 버전명
                = Optional.ofNullable(pdServiceVersions)
                    .map(versions->Arrays.stream(versions)
                            .filter(버전아이디_이름및일정_맵::containsKey)
                            .map(버전아이디_이름및일정_맵::get)
                            .collect(Collectors.joining(",")))
                    .orElse(" - ");

            String deletedDate = issue.지라이슈_삭제_일자_가져오기();

            // 버전명 세팅 종료
            ExcelDataDTO excelData = ExcelDataDTO.builder()
                    .pdServiceId(issue.getPdServiceId()) // 검토
                    .pdServiceName(제품명)
                    .pdServiceVersions(issue.getPdServiceVersions()) // 검토
                    .pdServiceVersionNames(버전명) // 가져온 이슈의 버전으로 보여준다는 점이 "중요"

                    // 요구사항 구분을 위해
                    .isReq(issue.getIsReq())
                    .parentReqKey(Optional.ofNullable(issue.getParentReqKey()).orElse(""))
                    .upperKey(Optional.ofNullable(issue.getUpperKey()).orElse(""))
                    .etc(issue.etcBoolean())

                    .cReqLink(issue.getCReqLink()) // 검토
                    .reqTitle(reqAddEntityValue.getC_title()) // 요구사항
                    .reqState(요구사항_상태_맵.get(reqAddEntityValue.getC_req_state_link())) // 요구사항 상태

                    .almProjectName(issue.getProject().getName())
                    .key(issue.getKey())
                    .issueID(issue.getIssueID())
                    .issueTitle(issue.getSummary())
                    .issueStatus(issue.getStatus().getName())
                    .assigneeName(Optional.ofNullable(issue.getAssignee()).map(지라이슈.담당자::getDisplayName).orElse("담당자 정보 없음"))
                    .assigneeEmail(Optional.ofNullable(issue.getAssignee()).map(지라이슈.담당자::getEmailAddress).orElse(""))

                    // 생성일 수정일 해결일 삭제일(있다면)
                    .createDate(Optional.ofNullable(issue.getCreated()).orElse(""))
                    .updatedDate(Optional.ofNullable(issue.getUpdated()).orElse(""))
                    .resolutionDate(Optional.ofNullable(issue.getResolutiondate()).orElse(""))
                    .deletedDate(deletedDate)
                    .build();

            if (issue.getIsReq().equals(true)) {
                excelDataList.add(excelData);
                reqKeyIndexMap.put(excelData.getKey(), excelDataList.size() - 1);
            } else {
                subDataList.add(excelData);
            }
        }

        // 요구사항 - 하위이슈 순으로 재배열
        for (ExcelDataDTO subData : subDataList) {
            String parentReqKey = subData.getParentReqKey();
            if (parentReqKey != null && reqKeyIndexMap.containsKey(parentReqKey)) {
                int index = reqKeyIndexMap.get(subData.getParentReqKey());
                // 요소를 index+1 위치에 삽입
                excelDataList.add(index + 1, subData);
                // 맵 갱신 (삽입된 요소 이후의 인덱스들이 변화하므로)
                for (int i = index + 1; i < excelDataList.size(); i++) {
                    reqKeyIndexMap.put(excelDataList.get(i).getKey(), i);
                }
            }
        }

        log.info("[ FullDataServiceImpl :: getExcelData ] :: issue size => {}", ALM_이슈목록.size());
        log.info("[ FullDataServiceImpl :: getExcelData ] :: excelData size => {}", excelDataList.size());

        SessionUtil.removeAttribute("excel-data");

        return excelDataList;
    }

    // key 가 pdServiceVerion c_id, value가 버전명(시작일~종료일) 인 맵 만들기.
    private Map<Long, String> 버전아이디_이름및일정_맵_구성 (Set<PdServiceVersionEntity> 버전엔티티_세트) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");


        // Map 초기화
        Map<Long, String> 버전키_이름및일정_맵 = new HashMap<>();

        for (PdServiceVersionEntity entity : 버전엔티티_세트) {
            Long key = entity.getC_id();

            String startDate = outputFormat.format(inputFormat.parse(entity.getC_pds_version_start_date()));
            String endDate = outputFormat.format(inputFormat.parse(entity.getC_pds_version_end_date()));

            String value = entity.getC_title() + " (" + startDate + " ~ " + endDate + ")";
            버전키_이름및일정_맵.put(key, value);
        }

        return 버전키_이름및일정_맵;
    }

}
