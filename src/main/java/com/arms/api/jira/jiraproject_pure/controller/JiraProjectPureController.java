/*
 * @author Dongmin.lee
 * @since 2023-03-21
 * @version 23.03.21
 * @see <pre>
 *  Copyright (C) 2007 by 313 DEV GRP, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by 313 developer group <313@313.co.kr>, December 2010
 * </pre>
 */
package com.arms.api.jira.jiraproject_pure.controller;

import com.arms.api.jira.jiraproject_pure.model.AllJiraProjectsDTO;
import com.arms.api.jira.jiraproject_pure.model.JiraProjectPureDTO;
import com.arms.api.jira.jiraproject_pure.model.JiraProjectPureEntity;
import com.arms.api.jira.jiraproject_pure.service.JiraProjectPure;
import com.arms.egovframework.javaservice.treeframework.controller.CommonResponse;
import com.arms.egovframework.javaservice.treeframework.controller.TreeAbstractController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(value = {"/arms/jiraProjectPure"})
public class JiraProjectPureController extends TreeAbstractController<JiraProjectPure, JiraProjectPureDTO, JiraProjectPureEntity> {

    @Autowired
    @Qualifier("jiraProjectPure")
    private JiraProjectPure jiraProjectPure;

    @PostConstruct
    public void initialize() {
        setTreeService(jiraProjectPure);
        setTreeEntity(JiraProjectPureEntity.class);
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ResponseBody
    @GetMapping("/getJiraProjects.do")
    public ResponseEntity<CommonResponse.ApiResult<List<AllJiraProjectsDTO>>> getJiraProjectPures(
            @RequestParam(name = "pdServiceId") Long pdServiceId,
            @RequestParam(name = "pdServiceVersionIds", required = false) List<Long> pdServiceVersionIds
    ) throws Exception {

        List<JiraProjectPureEntity> jiraProjectsByProduct = jiraProjectPure.getJiraProjects();

        List<AllJiraProjectsDTO> response = jiraProjectsByProduct.stream().map(entity -> modelMapper.map(entity, AllJiraProjectsDTO.class)).collect(Collectors.toList());

        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
