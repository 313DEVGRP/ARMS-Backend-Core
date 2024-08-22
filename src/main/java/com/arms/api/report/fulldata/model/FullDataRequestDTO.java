package com.arms.api.report.fulldata.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FullDataRequestDTO {

    private int size;

    private int page;

    @NotNull private Long pdServiceId;

    private List<Long> pdServiceVersionIds;

    private List<Long> almProjectIds;

    private List<String> almProjectUrls;

    private String startDate;

    private String endDate;

    private String [] emailAddress;

}
