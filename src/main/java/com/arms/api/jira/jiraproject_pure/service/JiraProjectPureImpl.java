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

@AllArgsConstructor
@Service("jiraProjectPure")
public class JiraProjectPureImpl extends TreeServiceImpl implements JiraProjectPure {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<JiraProjectPureEntity> getJiraProjects() throws Exception {

        JiraProjectPureEntity jiraProjectPureEntity = new JiraProjectPureEntity();

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