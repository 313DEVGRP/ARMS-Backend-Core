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
package com.arms.api.requirement.reqadd_pure.service;

import com.arms.api.requirement.reqadd_pure.model.ReqAddPureEntity;
import com.arms.egovframework.javaservice.treeframework.service.TreeService;

import javax.servlet.http.HttpServletRequest;

public interface ReqAddPure extends TreeService {

    public ReqAddPureEntity moveReqNode(ReqAddPureEntity reqAddEntity, String changeReqTableName, HttpServletRequest request) throws Exception;

    int updateDrawDB(ReqAddPureEntity reqAddPureEntity, String changeReqTableName) throws Exception;
}
