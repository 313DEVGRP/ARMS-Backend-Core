/*
 * @author Dongmin.lee
 * @since 2023-03-13
 * @version 23.03.13
 * @see <pre>
 *  Copyright (C) 2007 by 313 DEV GRP, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by 313 developer group <313@313.co.kr>, December 2010
 * </pre>
 */
package com.egovframework.ple.treeframework.service;

import com.egovframework.ple.treeframework.model.TreeSearchEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TreeService {

    public <T extends TreeSearchEntity> T getNode(T treeSearchEntity) throws Exception;

    public <T extends TreeSearchEntity> int updateNode(T treeSearchEntity) throws Exception;

    public <T extends TreeSearchEntity> List<T> getChildNode(T treeSearchEntity) throws Exception;

    public <T extends TreeSearchEntity> List<T> getChildNodeWithoutPaging(T treeSearchEntity) throws Exception;

    public <T extends TreeSearchEntity> List<T> getPaginatedChildNode(T treeSearchEntity) throws Exception;

    public <T extends TreeSearchEntity> List<String> searchNode(T treeSearchEntity) throws Exception;

    public <T extends TreeSearchEntity> T addNode(T treeSearchEntity) throws Exception;

    public <T extends TreeSearchEntity> int removeNode(T treeSearchEntity) throws Exception;

    public <T extends TreeSearchEntity> int alterNode(T treeSearchEntity) throws Exception;

    public <T extends TreeSearchEntity> int alterNodeType(T treeSearchEntity) throws Exception;

    public <T extends TreeSearchEntity> T moveNode(T treeSearchEntity, HttpServletRequest request) throws Exception;

}
