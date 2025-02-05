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
package com.arms.api.requirement.reqstate.service;

import com.arms.api.requirement.reqstate.model.ReqStateEntity;
import com.arms.egovframework.javaservice.treeframework.service.TreeService;

import java.util.List;
import java.util.Map;

public interface ReqState extends TreeService {

    Map<Long, ReqStateEntity> 완료상태조회() throws Exception;

    List<ReqStateEntity> 기본_상태_설정(ReqStateEntity reqStateEntity) throws Exception;
}