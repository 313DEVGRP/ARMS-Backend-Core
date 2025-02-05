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
package com.arms.api.requirement.reqstate.controller;


import com.arms.api.requirement.reqadd.service.ReqAdd;
import com.arms.api.requirement.reqstate.model.ReqStateDTO;
import com.arms.api.requirement.reqstate.model.ReqStateEntity;
import com.arms.api.requirement.reqstate.service.ReqState;
import com.arms.api.requirement.reqstate_category.model.ReqStateCategoryEntity;
import com.arms.api.requirement.reqstate_category.service.ReqStateCategory;
import com.arms.egovframework.javaservice.treeframework.controller.TreeAbstractController;
import com.arms.egovframework.javaservice.treeframework.validation.group.AddNode;
import com.arms.egovframework.javaservice.treeframework.validation.group.RemoveNode;
import com.arms.egovframework.javaservice.treeframework.validation.group.UpdateNode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(value = {"/arms/reqState"})
public class ReqStateController extends TreeAbstractController<ReqState, ReqStateDTO, ReqStateEntity> {

    @PostConstruct
    public void initialize() {
        setTreeService(reqState);
        setTreeEntity(ReqStateEntity.class);
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("reqState")
    private ReqState reqState;

    @Autowired
    @Qualifier("reqStateCategory")
    private ReqStateCategory reqStateCategory;

    @Autowired
    @Qualifier("reqAdd")
    private ReqAdd reqAdd;

    @ResponseBody
    @RequestMapping(value = "/addStateNode.do", method = RequestMethod.POST)
    public ModelAndView addStateNode(@Validated(value = AddNode.class) ReqStateDTO reqStateDTO,
                                   BindingResult bindingResult, HttpServletRequest request, ModelMap model) throws Exception {

        log.info("ReqStateController :: addStateNode");
        ReqStateEntity reqStateEntity = modelMapper.map(reqStateDTO, ReqStateEntity.class);

        if (reqStateDTO.getC_state_category_mapping_id() != null) {
            ReqStateCategoryEntity searchEntity = new ReqStateCategoryEntity();
            searchEntity.setC_id(reqStateDTO.getC_state_category_mapping_id());
            ReqStateCategoryEntity reqStateCategoryEntity = reqStateCategory.getNode(searchEntity);
            reqStateEntity.setReqStateCategoryEntity(reqStateCategoryEntity);
        }

        if (reqStateDTO.getC_check() == null) {
            reqStateDTO.setC_check("false");
        }

        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("result", reqState.addNode(reqStateEntity));
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value = "/updateNode.do", method = RequestMethod.PUT)
    public ModelAndView updateNode(@Validated(value = UpdateNode.class) ReqStateDTO reqStateDTO,
                                   BindingResult bindingResult, HttpServletRequest request, ModelMap model) throws Exception {

        log.info("ReqStateController :: updateNode");
        ReqStateEntity reqStateEntity = modelMapper.map(reqStateDTO, ReqStateEntity.class);

        if (reqStateDTO.getC_state_category_mapping_id() != null) {
            ReqStateCategoryEntity searchEntity = new ReqStateCategoryEntity();
            searchEntity.setC_id(reqStateDTO.getC_state_category_mapping_id());
            ReqStateCategoryEntity reqStateCategoryEntity = reqStateCategory.getNode(searchEntity);
            reqStateEntity.setReqStateCategoryEntity(reqStateCategoryEntity);
        }

        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("result", reqState.updateNode(reqStateEntity));
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value = "/removeNodeAndChangeState.do", method = RequestMethod.DELETE)
    public ModelAndView removeNodeAndChangeState(@Validated(value = RemoveNode.class) ReqStateDTO reqStateDTO,
                                   @RequestParam(value = "stateIdToChange") Long stateIdToChagne,
                                   BindingResult bindingResult, ModelMap model) throws Exception {

        log.info("ReqStateController :: removeNodeAndChangeState");
        ReqStateEntity reqStateEntity = modelMapper.map(reqStateDTO, ReqStateEntity.class);

        reqStateEntity.setStatus(reqState.removeNode(reqStateEntity));
        setJsonDefaultSetting(reqStateEntity);

        reqAdd.상태삭제_후_전체_요구사항_상태변경(reqStateEntity.getC_id(), stateIdToChagne);

        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("result", reqStateEntity);
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/complete-keyword"},
            method = {RequestMethod.GET}
    )
    public ModelAndView 요구사항_완료_키워드조회() throws Exception {
        logger.info(" [ " + this.getClass().getName() + " :: 요구사항_완료_키워드조회 ]");
        Set<String> 완료_키워드_셋 = new HashSet<>();
        Map<Long, ReqStateEntity> 완료상태맵 = reqState.완료상태조회();
        완료상태맵.forEach((key, value) -> {
            완료_키워드_셋.add(value.getC_title());
        });

        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("result", 완료_키워드_셋);
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/getReqStateListFilter.do"},
            method = {RequestMethod.GET}
    )
    public ResponseEntity<?> 카테고리_매핑된_상태목록_조회() throws Exception {

        logger.info(" [ 카테고리_매핑된_상태목록_조회 ]");
        ReqStateEntity reqStateEntity = new ReqStateEntity();
        List<ReqStateEntity> 전체_상태목록 = reqState.getNodesWithoutRoot(reqStateEntity);

        // 매핑된 카테고리가 있는 상태만 필터링 및 카테고리 아이디 오름차순 정렬
        List<ReqStateEntity> 카테고리_매핑된_상태목록 = 전체_상태목록.stream()
                                                        .filter(Objects::nonNull)
                                                        .filter(reqState -> reqState.getReqStateCategoryEntity() != null)
                                                        .sorted((state1, state2) -> Long.compare(
                                                                state1.getReqStateCategoryEntity().getC_id(),
                                                                state2.getReqStateCategoryEntity().getC_id())
                                                        )
                                                        .collect(Collectors.toList());

        return ResponseEntity.ok(카테고리_매핑된_상태목록);
    }

    @ResponseBody
    @RequestMapping(value = "/defaultSetting.do", method = RequestMethod.PUT)
    public ResponseEntity<?> 기본_상태_설정(ReqStateDTO reqStateDTO) throws Exception {

        log.info("ReqStateController :: defaultSetting");
        ReqStateEntity reqStateEntity = modelMapper.map(reqStateDTO, ReqStateEntity.class);

        List<ReqStateEntity> reqStateEntities = reqState.기본_상태_설정(reqStateEntity);
        return ResponseEntity.ok(reqStateEntities);
    }
}