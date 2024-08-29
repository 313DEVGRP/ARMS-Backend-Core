package com.arms.api.report.fulldata.service;

import com.arms.api.report.fulldata.model.ExcelDataDTO;
import com.arms.api.report.fulldata.model.FullDataAssigneesResponse;
import com.arms.api.report.fulldata.model.FullDataRequestDTO;
import com.arms.api.report.fulldata.model.작업자_정보;
import com.arms.api.util.communicate.external.response.jira.지라이슈;

import java.util.List;

public interface FullDataService {

    List<작업자_정보> getAssigneeList();

    List<ExcelDataDTO> getExcelData(String changeReqTableName, FullDataRequestDTO fullDataRequestDTO) throws Exception;
}
