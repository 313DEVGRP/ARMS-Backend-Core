package com.arms.api.util.communicate.external.response.jira;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class 암스_요구사항_속성정보 {
    // 지라이슈 인덱스 생성 시, 암스_요구사항_속성 정보 전달을 위한 DTO
    private Long cReqLink;
    private Long pdServiceId;
    private Long[] pdServiceVersions;
    private Long cReqPriorityLink;
    private String cReqPriorityName;
    private Long cReqDifficultyLink;
    private String cReqDifficultyName;
    private Long cReqStateLink;
    private String cReqStateName;

}
