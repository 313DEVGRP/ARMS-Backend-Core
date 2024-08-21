package com.arms.api.report.fulldata.service;

import com.arms.api.report.fulldata.model.FullDataAssigneesResponse;
import com.arms.api.report.fulldata.model.FullDataRequestDTO;
import com.arms.api.util.communicate.external.response.jira.지라이슈;

import java.util.List;

public interface FullDataService {

    List<FullDataAssigneesResponse> getAssignees(FullDataRequestDTO fullDataRequestDTO);
    List<지라이슈> getExcelData(FullDataRequestDTO fullDataRequestDTO) throws Exception;
}
