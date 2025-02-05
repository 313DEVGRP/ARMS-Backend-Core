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
package com.arms.api.requirement.reqadd.service;

import com.arms.api.product_service.pdservice.model.PdServiceEntity;
import com.arms.api.requirement.reqadd.model.*;
import com.arms.api.requirement.reqadd.model.요구사항별_담당자_목록.요구사항_담당자;
import com.arms.api.requirement.reqadd_pure.model.ReqAddPureEntity;
import com.arms.egovframework.javaservice.treeframework.service.TreeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ReqAdd extends TreeService {

    public ReqAddEntity addReqNode(ReqAddEntity reqAddEntity, String changeReqTableName) throws Exception;

    public ReqAddEntity moveReqNode(ReqAddEntity reqAddEntity, String changeReqTableName, HttpServletRequest request) throws Exception;

    public int removeReqNode(ReqAddEntity reqAddEntity, String changeReqTableName, HttpServletRequest request) throws Exception;

    public Integer updateReqNode(ReqAddEntity reqAddEntity, String changeReqTableName) throws Exception;

    public Integer updateDataBase( ReqAddEntity reqAddEntity, String changeReqTableName) throws Exception;

    public ReqAddDetailDTO getDetail(FollowReqLinkDTO followReqLinkDTO, String changeReqTableName) throws Exception;

    public List<요구사항_담당자> getRequirementAssignee(PdServiceEntity pdServiceEntity) throws Exception;

    public void 요구사항_생성_이후_상태정보_처리_프로세스(ReqAddEntity savedReqAddEntity) throws Exception;

    public void 요구사항_수정_이후_상태정보_처리_프로세스(String changeReqTableName, ReqAddEntity savedReqAddEntity, LoadReqAddDTO loadReqAddDTO) throws Exception;

    public void 요구사항_삭제_이후_상태정보_처리_프로세스(String changeReqTableName, ReqAddEntity savedReqAddEntity, LoadReqAddDTO loadReqAddDTO) throws Exception;

    public void 요구사항_상태_디비_업데이트(ReqAddEntity reqAddEntity, String changeReqTableName) throws Exception;

    public void 상태삭제_후_전체_요구사항_상태변경(Long 변경_전_상태아이디, Long 변경_후_상태아이디) throws Exception;

    public List<ReqAddPureEntity> 요구사항_상태변경(Long 변경_전_상태아이디, Long 변경할_상태아이디) throws Exception;

}
