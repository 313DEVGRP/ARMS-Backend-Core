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
package com.arms.api.jira.jiraproject_pure.service;

import com.arms.api.globaltreemap.model.GlobalTreeMapEntity;
import com.arms.api.globaltreemap.service.GlobalTreeMapService;
import com.arms.api.jira.jiraproject_pure.model.JiraProjectPureEntity;
import com.arms.egovframework.javaservice.treeframework.service.TreeServiceImpl;
import lombok.AllArgsConstructor;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service("jiraProjectPure")
public class JiraProjectPureImpl extends TreeServiceImpl implements JiraProjectPure {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GlobalTreeMapService globalTreeMapService;


    @Override
    public List<JiraProjectPureEntity> getJiraProjects(Long pdServiceId, List<Long> pdServiceVersionIds) throws Exception {
        if (pdServiceVersionIds == null || pdServiceVersionIds.isEmpty()) {
            pdServiceVersionIds = globalTreeMapService.findAllByIds(List.of(pdServiceId), "pdservice_link").stream()
                    .filter(entity -> entity.getPdserviceversion_link() != null)
                    .map(GlobalTreeMapEntity::getPdserviceversion_link)
                    .collect(Collectors.toList());
        }

        List<Long> jiraProjectIds = globalTreeMapService.findAllByIds(pdServiceVersionIds, "pdserviceversion_link").stream()
                .filter(entity -> entity.getJiraproject_link() != null)
                .map(GlobalTreeMapEntity::getJiraproject_link)
                .collect(Collectors.toList());

        if (jiraProjectIds.isEmpty()) {
            return Collections.emptyList();
        }

        JiraProjectPureEntity jiraProjectPureEntity = new JiraProjectPureEntity();

        Criterion criterion = Restrictions.in("c_id", jiraProjectIds);

        jiraProjectPureEntity.getCriterions().add(criterion);

        return getChildNode(jiraProjectPureEntity);
    }

    @Override
    public List<JiraProjectPureEntity> getJiraProjects(List<Long> ids) throws Exception {

        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        JiraProjectPureEntity jiraProjectPureEntity = new JiraProjectPureEntity();

        Criterion criterion = Restrictions.in("c_id", ids);

        jiraProjectPureEntity.getCriterions().add(criterion);

        return getChildNode(jiraProjectPureEntity);
    }
}