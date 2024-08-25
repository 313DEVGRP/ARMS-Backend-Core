package com.arms.api.report.fulldata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelDataDTO {

    private Long pdServiceId; // 검토
    private String pdServiceName;
    private Long[] pdServiceVersions;
    private String pdServiceVersionNames;

    private Boolean isReq;
    private Boolean etc;

    private String issueID;
    private String key;
    private String parentReqKey;
    private String upperKey;

    private String isReqName;

    private String almProjectName;

    private Long cReqLink;
    private String reqTitle;
    private String reqState;

    private String issueTitle;
    private String issueStatus;

    private String assigneeName;
    private String assigneeEmail;

    private String createDate;
    private String updatedDate;
    private String resolutionDate;
    private String deletedDate;
}
