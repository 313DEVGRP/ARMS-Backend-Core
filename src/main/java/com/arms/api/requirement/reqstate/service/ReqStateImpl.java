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
import com.arms.egovframework.javaservice.treeframework.service.TreeServiceImpl;
import com.arms.egovframework.javaservice.treeframework.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service("reqState")
public class ReqStateImpl extends TreeServiceImpl implements ReqState{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Map<Long, ReqStateEntity> 완료상태조회() throws Exception {
		ReqStateEntity reqStateEntity = new ReqStateEntity();
		List<ReqStateEntity> 전체상태목록 = this.getNodesWithoutRoot(reqStateEntity);
		Map<Long, ReqStateEntity> 완료상태맵 = this.완료상태_카테고리_필터링(전체상태목록);

		return 완료상태맵;
	}

	private Map<Long, ReqStateEntity> 완료상태_카테고리_필터링(List<ReqStateEntity> 전체상태목록) {
		return 전체상태목록.stream()
				.filter(상태 -> 상태.getReqStateCategoryEntity() != null && 상태.getReqStateCategoryEntity().getC_closed() != null
									&& StringUtils.equals(상태.getReqStateCategoryEntity().getC_closed(), "true"))
				.collect(Collectors.toMap(ReqStateEntity::getC_id, 상태엔티티 -> 상태엔티티));
	}

	@Override
	@Transactional
	public List<ReqStateEntity> 기본_상태_설정(ReqStateEntity reqStateEntity) throws Exception {
		ReqStateEntity reqState = new ReqStateEntity();
		List<ReqStateEntity> 전체상태목록 = this.getNodesWithoutRoot(reqState);

		List<ReqStateEntity> 기본값_설정_상태목록 = 전체상태목록.stream()
									.map(상태 -> {
										if (Objects.equals(reqStateEntity.getC_id(), 상태.getC_id())) {
											상태.setC_check("true");
										}
										else {
											상태.setC_check("false");
										}
										return 상태;
									})
									.collect(Collectors.toList());

		List<ReqStateEntity> reqStateEntities = this.saveOrUpdateList(기본값_설정_상태목록);
		return reqStateEntities;
	}
}