package com.arms.api.report.fulldata.service;

import com.arms.api.util.communicate.external.EngineService;
import com.arms.api.util.communicate.external.response.jira.지라이슈;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("FullDataService")
@RequiredArgsConstructor
public class FullDataServiceImpl implements FullDataService {

    private final EngineService engineService;

    @Override
    public void jobList(Long pdServiceId){
        ResponseEntity<List<지라이슈>> 이슈조회 = engineService.이슈조회(pdServiceId);
        System.out.println(이슈조회);
    }
}
