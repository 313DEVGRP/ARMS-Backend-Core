/*
 * @author Dongmin.lee
 * @since 2023-10-22
 * @version 23.10.22
 * @see <pre>
 *  Copyright (C) 2007 by 313 DEV GRP, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by 313 developer group <313@313.co.kr>, December 2010
 * </pre>
 */
package com.arms.api.requirement.reqstate_category.controller;


import com.arms.api.requirement.reqstate_category.model.ReqStateCategoryDTO;
import com.arms.api.requirement.reqstate_category.model.ReqStateCategoryEntity;
import com.arms.api.requirement.reqstate_category.service.ReqStateCategory;
import com.arms.egovframework.javaservice.treeframework.controller.TreeAbstractController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;

@Slf4j
@Controller
@RequestMapping(value = {"/arms/reqStateCategory"})
public class ReqStateCategoryController extends TreeAbstractController<ReqStateCategory, ReqStateCategoryDTO, ReqStateCategoryEntity> {

    @Autowired
    @Qualifier("reqStateCategory")
    private ReqStateCategory reqStateCategory;

    @PostConstruct
    public void initialize() {
        setTreeService(reqStateCategory);
		setTreeEntity(ReqStateCategoryEntity.class);
    }

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
}
