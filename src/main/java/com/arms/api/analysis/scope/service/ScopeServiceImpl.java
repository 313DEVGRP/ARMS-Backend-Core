package com.arms.api.analysis.scope.service;


import com.arms.api.analysis.scope.dto.TreeBarDTO;
import com.arms.api.product_service.pdservice.model.PdServiceEntity;
import com.arms.api.product_service.pdservice.service.PdService;
import com.arms.api.product_service.pdserviceversion.model.PdServiceVersionEntity;
import com.arms.api.requirement.reqadd.model.ReqAddEntity;
import com.arms.api.requirement.reqadd.service.ReqAdd;
import com.arms.api.requirement.reqstatus.model.ReqStatusDTO;
import com.arms.api.requirement.reqstatus.model.ReqStatusEntity;
import com.arms.api.util.external_communicate.dto.search.검색결과;
import com.arms.api.util.external_communicate.dto.search.검색결과_목록_메인;
import com.arms.api.util.external_communicate.dto.제품_서비스_버전;
import com.arms.api.util.external_communicate.dto.요구_사항;
import com.arms.api.util.external_communicate.dto.지라이슈_제품_및_제품버전_검색요청;
import com.arms.api.util.external_communicate.내부통신기;
import com.arms.api.util.external_communicate.통계엔진통신기;
import com.arms.egovframework.javaservice.treeframework.interceptor.SessionUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScopeServiceImpl implements ScopeService {

    @Autowired
    private ReqAdd reqAdd;

    private static final SecureRandom RANDOM = new SecureRandom();

    private final PdService pdService;
    private final 내부통신기 내부통신기;
    private final 통계엔진통신기 통계엔진통신기;
    private final Gson gson;

    @Override
    public List<TreeBarDTO> treeBar(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청) throws Exception {
        ReqStatusDTO reqStatusDTO = new ReqStatusDTO();
        Long pdServiceLink = 지라이슈_제품_및_제품버전_검색요청.getPdServiceLink();
        List<Long> pdServiceVersionLinks = 지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks();
    
        List<ReqStatusEntity> reqStatuses = 내부통신기.제품별_요구사항_이슈_조회("T_ARMS_REQSTATUS_" + pdServiceLink, reqStatusDTO);
    
        List<TreeBarDTO> treeBarList = new ArrayList<>();
    
        // 1. 제품 조회
        PdServiceEntity pdServiceEntity = new PdServiceEntity();
        pdServiceEntity.setC_id(pdServiceLink);
        PdServiceEntity product = pdService.getNode(pdServiceEntity);
    
        // 2. 제품 등록
        treeBarList.add(addProduct(product));
    
        // 3. 제품 버전 조회
        List<PdServiceVersionEntity> productVersions = product.getPdServiceVersionEntities().stream().filter(
                pdServiceVersionEntity -> 지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks().contains(pdServiceVersionEntity.getC_id())
        ).sorted(Comparator.comparing(PdServiceVersionEntity::getC_id)).collect(Collectors.toList());
    
        // 4. 요구사항 조회
        List<ReqStatusEntity> reqStatusEntityList = getRequirements(reqStatuses, pdServiceVersionLinks);
    
        // 4-1. 요구사항 DTO로 변환
        List<TreeBarDTO> requirements = reqStatusEntityList.stream().map(TreeBarDTO::new).collect(Collectors.toList());
    
        // 5. 각 요구사항 별 담당자와 빈도수를 조회 (Top 10)
        ResponseEntity<검색결과_목록_메인> 외부API응답 = 통계엔진통신기.제품_혹은_제품버전들의_집계_flat(지라이슈_제품_및_제품버전_검색요청);
    
        검색결과_목록_메인 검색결과목록메인 = Optional.ofNullable(외부API응답.getBody()).orElse(new 검색결과_목록_메인());
    
        Map<String, List<검색결과>> 검색결과 = Optional.ofNullable(검색결과목록메인.get검색결과()).orElse(Collections.emptyMap());
    
        List<검색결과> groupByParentReqKey = Optional.ofNullable(검색결과.get("group_by_parentReqKey")).orElse(Collections.emptyList());
    
        // 6. 담당자가 많은 요구사항 Top 10 추출. 담당자가 없는 요구사항은 제외됩니다.
        List<검색결과> top10Requirements = groupByParentReqKey.stream()
                .sorted((a, b) -> Integer.compare(
                        b.get하위검색결과().get("group_by_assignee.assignee_displayName.keyword").size(),
                        a.get하위검색결과().get("group_by_assignee.assignee_displayName.keyword").size()))
                .limit(10)
                .collect(Collectors.toList());
    
        // 7. top10 요소들의 필드명 추출
        List<String> issueKeys = top10Requirements.stream()
                .map(com.arms.api.util.external_communicate.dto.search.검색결과::get필드명)
                .collect(Collectors.toList());
    
    
        // 8. requirements 리스트를 필터링하여 id 값이 issueKeys 에 있는 요소만 선택
        List<TreeBarDTO> filteredRequirements = filteredRequirements(requirements, issueKeys);
    
        // 9. Top 10 요구사항의 버전 정보 추출하여 리스트에 추가
        List<TreeBarDTO> versionList = addProductVersions(product, productVersions, filteredRequirements);
        treeBarList.addAll(versionList);
    
        // 10. 요구사항 추가
        treeBarList.addAll(filteredRequirements);
    
        // 11. 담당자 추가
        treeBarList.addAll(addAssignees(top10Requirements));
    
        return treeBarList;
     }
    
    private List<TreeBarDTO> addProductVersions(
        PdServiceEntity product,
        List<PdServiceVersionEntity> productVersions,
        List<TreeBarDTO> filteredRequirements
    ) {
    
        List<TreeBarDTO> 트리바제품버전데이터 = new ArrayList<>();
    
        String 제품아이디 = String.valueOf(product.getC_id());
    
        Set<String> 버전중복제거 = new HashSet<>();
    
        filteredRequirements.stream()
                .filter(filteredRequirement -> 버전중복제거.add(filteredRequirement.getParent()))
                .forEach(filteredRequirement -> {
                    String 요구사항의버전아이디 = filteredRequirement.getParent().replace("[", "").replace("]", "");
                    
                    List<String> 제품버전명목록 = productVersions.stream()
                            .filter(version -> 요구사항의버전아이디.contains(String.valueOf(version.getC_id())))
                            .map(PdServiceVersionEntity::getC_title)
                            .collect(Collectors.toList());
                    
                    String 요구사항의버전명 = String.join(", ", 제품버전명목록);
                    
                    트리바제품버전데이터.add(TreeBarDTO.builder()
                            .id(filteredRequirement.getParent()) // ["17","18","19"]
                            .name(요구사항의버전명) // "17","18","19"
                            .color("")
                            .type("version")
                            .parent(제품아이디)
                            .build());
                });
    
        return 트리바제품버전데이터;
    }
    
    private TreeBarDTO addProduct(PdServiceEntity product) {
         return TreeBarDTO.builder()
                 .id(product.getC_id().toString())
                 .name(product.getC_title())
                 .type("product")
                 .color("")
                 .parent("")
                 .build();
     }
    
    private List<ReqStatusEntity> getRequirements(List<ReqStatusEntity> reqStatuses, List<Long> pdServiceVersionLinks) {
        return reqStatuses.stream()
                .filter(entity -> {
                    List<Long> versionSet = gson.fromJson(entity.getC_req_pdservice_versionset_link(), new TypeToken<List<Long>>(){}.getType());
                    return versionSet.stream().anyMatch(pdServiceVersionLinks::contains);
                })
                .collect(Collectors.toList());
    }
    
    private List<TreeBarDTO> filteredRequirements(List<TreeBarDTO> requirements, List<String> issueKeys) {
         return requirements.stream()
                 .filter(req -> issueKeys.contains(req.getId()))
                 .collect(Collectors.toList());
     }
    
    private List<TreeBarDTO> addAssignees(List<검색결과> top10Requirements) {
        Map<String, String> assigneeToColorMap = new HashMap<>();
        return top10Requirements.stream()
                .flatMap(parentReqKey -> {
                    String parent = parentReqKey.get필드명();
                    return parentReqKey.get하위검색결과().get("group_by_assignee.assignee_displayName.keyword").stream()
                            .map(assignee -> {
                                String name = assignee.get필드명();
                                String color = assigneeToColorMap.computeIfAbsent(name, k ->
                                        String.format("#%02x%02x%02x", RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256))
                                );
                                long value = assignee.get개수();
                                return TreeBarDTO.builder()
                                        .id(parent + assignee.get필드명())
                                        .parent(parent)
                                        .type("assignee")
                                        .name(name + " (" + value + ")")
                                        .value(value)
                                        .color(color)
                                        .build();
                            });
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<제품_서비스_버전> 요구사항_상태_매핑(List<제품_서비스_버전> 제품_서비스_버전_목록, Map<String, List<검색결과>> 검색결과) {

        // 버전별_요구사항_상태
        List<검색결과> 버전별_검색_결과 = 검색결과.get("group_by_pdServiceVersions");
        Map<String, List<Map<String, String>>> 버전_맵 = new HashMap<>();
        for ( 검색결과 버전_결과요소 : 버전별_검색_결과 ) {
            // 필드, 개수, 하위검색결과
            String 버전_아이디 = 버전_결과요소.get필드명();// 버전명
            List<Map<String, String>> 이슈키_이슈상태_목록 = new ArrayList<>();

            List<검색결과> 해당_버전_이슈_목록 = 버전_결과요소.get하위검색결과().get("group_by_key");
            for (검색결과 각각_이슈 : 해당_버전_이슈_목록) {
                Map<String, String> 이슈_키_상태 = new HashMap<>();
                String issueKey = 각각_이슈.get필드명(); // 이슈 이름
                String statusName = 각각_이슈.get하위검색결과().get("group_by_status.status_name.keyword").get(0).get필드명();
                이슈_키_상태.put(issueKey, statusName);
                이슈키_이슈상태_목록.add(이슈_키_상태);
            }
            버전_맵.put(버전_아이디,이슈키_이슈상태_목록);
        }

        //제품_서비스_버전의 요구_사항에 요구_사항_상태 매핑하기
        for (제품_서비스_버전 버전_요소 : 제품_서비스_버전_목록) {
            List<Map<String, String>> 이슈_키_상태_맵_목록 = 버전_맵.get(String.valueOf(버전_요소.get제품_서비스_버전()));

            List<요구_사항> 요구_사항들 = 버전_요소.get요구사항들();
            for(int i = 0; i < 요구_사항들.size(); i++) {
                요구_사항 요구_사항 = 요구_사항들.get(i);
                String 요구_사항_키 = 요구_사항.get요구_사항_번호();

                for(int j = 0; j < 이슈_키_상태_맵_목록.size(); j++) {
                    Map<String, String> 이슈_키_상태_맵 = 이슈_키_상태_맵_목록.get(j);
                    if(이슈_키_상태_맵.containsKey(요구_사항_키)){
                        요구_사항.set요구_사항_상태(이슈_키_상태_맵.get(요구_사항_키));
                        이슈_키_상태_맵_목록.remove(j);
                        break;
                    }
                }
            }

        }

        return 제품_서비스_버전_목록;
    }

    @Override
    public void 버전_요구사항_자료(String changeReqTableName, Long pdServiceId, List<Long> pdServiceVersionLinks) throws Exception {

        SessionUtil.setAttribute("getReqAddListByFilter",changeReqTableName);

        PdServiceEntity 검색용도_제품 = new PdServiceEntity();
        검색용도_제품.setC_id(pdServiceId);
        PdServiceEntity 제품_검색결과 = pdService.getNode(검색용도_제품);
        Set<PdServiceVersionEntity> 제품_버전_세트 = 제품_검색결과.getPdServiceVersionEntities();
        Map<Long, String> 버전_아이디_이름_맵 = 제품_버전_세트.stream()
                .collect(Collectors.toMap(PdServiceVersionEntity::getC_id, PdServiceVersionEntity::getC_title));

        ReqAddEntity 검색용도_객체 = new ReqAddEntity();

        if (pdServiceVersionLinks != null && !pdServiceVersionLinks.isEmpty()) {
            Disjunction orCondition = Restrictions.disjunction();
            for (Long 버전 : pdServiceVersionLinks) {
                String 버전_문자열 = "\\\"" + String.valueOf(버전) + "\\\"";
                orCondition.add(Restrictions.like("c_req_pdservice_versionset_link", 버전_문자열, MatchMode.ANYWHERE));
            }
            검색용도_객체.getCriterions().add(orCondition);
        }

        List<ReqAddEntity> 검색_결과_목록 = reqAdd.getChildNode(검색용도_객체);

        Map<String, Long> 버전_요구사항_맵 = new HashMap<>();
        for(ReqAddEntity 요구사항 : 검색_결과_목록) {
            List<String> 버전목록 = List.of(요구사항.getC_req_pdservice_versionset_link().split(","));
            Collections.sort(버전목록);
            
            // 정렬된 값을 문자열로 만듭니다.
            StringBuilder keyBuilder = new StringBuilder();
            for (String 버전 : 버전목록) {
                String 버전이름 = 버전_아이디_이름_맵.get(Long.parseLong(버전));
                keyBuilder.append("\""+버전이름+"\" ");
            }
            String key = keyBuilder.toString();
            log.info("[ScopeServiceImple  :: 버전_요구사항_자료] :: 만들어진Key ==> {}", key);
            // Map에 해당하는 값을 증가시킵니다.
            버전_요구사항_맵.put(key, 버전_요구사항_맵.getOrDefault(key, 0L) + 1);
            
        }

        SessionUtil.removeAttribute("getReqAddListByFilter");
        log.info("[ScopeServiceImple  :: 버전_요구사항_자료] :: 버전_요구사항_맵 ==> {}", 버전_요구사항_맵.toString());

    }
}
