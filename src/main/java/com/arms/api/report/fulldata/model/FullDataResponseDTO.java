package com.arms.api.report.fulldata.model;

import com.arms.api.util.communicate.external.response.jira.지라이슈;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FullDataResponseDTO {

    Long totalHits;
    List<지라이슈> issueEntityList;
}
