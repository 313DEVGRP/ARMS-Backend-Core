package com.arms.util.external_communicate;

import com.arms.dashboard.model.combination.RequirementJiraIssueAggregationResponse;
import com.arms.dashboard.model.donut.AggregationResponse;
import com.arms.dashboard.model.power.Worker;
import com.arms.dashboard.model.sankey.SankeyElasticSearchData;

import com.arms.util.external_communicate.dto.search.검색결과_목록_메인;
import com.arms.util.external_communicate.dto.지라이슈_일반_검색_요청;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@FeignClient(name = "engine-dashboard", url = "${arms.engine.url}")
public interface 통계엔진통신기 {
    @GetMapping("/engine/jira/dashboard/jira-issue-statuses")
    public List<AggregationResponse> 제품_혹은_제품버전들의_지라이슈상태_집계(
            @RequestParam Long pdServiceLink,
            @RequestParam List<Long> pdServiceVersionLinks
    );

    @GetMapping("/engine/jira/dashboard/requirements-jira-issue-statuses")
    public Map<String, RequirementJiraIssueAggregationResponse> 제품_혹은_제품버전들의_요구사항_지라이슈상태_월별_집계(
            @RequestParam Long pdServiceLink,
            @RequestParam List<Long> pdServiceVersionLinks
    );

    @GetMapping("/engine/jira/dashboard/issue-assignee/{pdServiceId}")
    public Map<String, Long> 제품서비스별_담당자_이름_통계(@PathVariable("pdServiceId") Long 제품서비스_아이디);

    @GetMapping("/engine/jira/dashboard/version-assignees")
    public Map<String, List<SankeyElasticSearchData>> 제품_혹은_제품버전들의_담당자목록(
            @RequestParam Long pdServiceLink,
            @RequestParam List<Long> pdServiceVersionLinks,
            @RequestParam int maxResults
    );

    @GetMapping("/engine/jira/dashboard/assignee-jira-issue-statuses")
    public Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별집계(
            @RequestParam Long pdServiceLink) throws IOException;

    @GetMapping("/engine/jira/dashboard/normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 제품서비스_일반_통계(
            @PathVariable("pdServiceId") Long 제품서비스_아이디 ,
            @SpringQueryMap 지라이슈_일반_검색_요청 지라이슈_검색_일반_요청
    );
    
    @GetMapping("/engine/jira/dashboard/isreq-normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 제품서비스_일반_통계_개선(
            @PathVariable("pdServiceId") Long 제품서비스_아이디 ,
            @SpringQueryMap 지라이슈_일반_검색_요청 지라이슈_검색_일반_요청
    );

    @GetMapping("/engine/jira/dashboard/exclusion-isreq-normal/{pdServiceId}")
    public ResponseEntity<Map<String, Object>> 제품서비스_요구사항제회_일반_통계(
            @PathVariable("pdServiceId") Long 제품서비스_아이디 ,
            @SpringQueryMap 지라이슈_일반_검색_요청 지라이슈_검색_일반_요청);

    @GetMapping("/engine/jira/dashboard/assignees-requirements-involvements")
    public List<Worker> 작업자별_요구사항_관여도(
            @RequestParam Long pdServiceLink,
            @RequestParam List<Long> pdServiceVersionLinks
    ) throws IOException;
}
