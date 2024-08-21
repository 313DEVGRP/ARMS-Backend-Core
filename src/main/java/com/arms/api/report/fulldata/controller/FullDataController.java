package com.arms.api.report.fulldata.controller;

import com.arms.api.report.fulldata.model.FullDataAssigneesResponse;
import com.arms.api.report.fulldata.model.FullDataRequestDTO;
import com.arms.api.report.fulldata.service.FullDataService;
import com.arms.api.util.communicate.external.response.jira.지라이슈;
import com.arms.egovframework.javaservice.treeframework.controller.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = {"/arms/report/full-data"})
public class FullDataController {

    private final FullDataService fullDataService;


    @GetMapping("/assignees")
    public ResponseEntity<CommonResponse.ApiResult<List<FullDataAssigneesResponse>>> getAssignees(@Validated FullDataRequestDTO fullDataRequestDTO) {
        List<FullDataAssigneesResponse> response = fullDataService.getAssignees(fullDataRequestDTO);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @GetMapping("/{changeReqTableName}/excel-data")
    public ResponseEntity<CommonResponse.ApiResult<List<지라이슈>>> getExcelData(
            @PathVariable(value = "changeReqTableName") String changeReqTableName,
            @Validated FullDataRequestDTO fullDataRequestDTO) throws Exception {
        List<지라이슈> response = fullDataService.getExcelData(changeReqTableName, fullDataRequestDTO);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

}
