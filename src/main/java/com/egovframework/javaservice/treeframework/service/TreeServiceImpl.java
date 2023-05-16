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
package com.egovframework.javaservice.treeframework.service;

import com.egovframework.javaservice.treeframework.dao.TreeDao;
import com.egovframework.javaservice.treeframework.model.TreeSearchEntity;
import com.egovframework.javaservice.treeframework.interceptor.RouteTableInterceptor;
import com.egovframework.javaservice.treeframework.util.PaginationInfo;
import com.egovframework.javaservice.treeframework.util.StringUtils;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.CacheMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.unitils.util.ReflectionUtils;

import javax.annotation.Resource;
import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.*;

@Service("treeService")
public class TreeServiceImpl implements TreeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("rawtypes")
    @Resource(name = "treeDao")
    private TreeDao treeDao;

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TreeSearchEntity> T getNode(T treeSearchEntity) throws Exception {

        logger.info("CoreServiceImpl :: getNode");
        treeDao.setClazz(treeSearchEntity.getClass());
        treeSearchEntity.setWhere("c_id", treeSearchEntity.getC_id());
        Object uniqueObj = treeDao.getUnique(treeSearchEntity);
        return (T) uniqueObj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TreeSearchEntity> List<T> getChildNodeWithoutPaging(T treeSearchEntity) throws Exception {
        treeDao.setClazz(treeSearchEntity.getClass());
        List<T> list = treeDao.getListWithoutPaging(treeSearchEntity);
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TreeSearchEntity> List<T> getChildNode(T treeSearchEntity) throws Exception {
        treeDao.setClazz(treeSearchEntity.getClass());
        treeSearchEntity.setOrder(Order.desc("c_position"));
        List<T> list = treeDao.getList(treeSearchEntity);
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TreeSearchEntity> List<T> getPaginatedChildNode(T treeSearchEntity) throws Exception {

        treeDao.setClazz(treeSearchEntity.getClass());
        treeDao.getCurrentSession().setCacheMode(CacheMode.IGNORE);
        int totalCount = treeDao.getCount(treeSearchEntity);

        int autoPageSize = (int) Math.ceil(totalCount / treeSearchEntity.getPageUnit());

        /** paging */
        PaginationInfo paginationInfo = treeSearchEntity.getPaginationInfo();
        paginationInfo.setTotalRecordCount(totalCount);
        paginationInfo.setCurrentPageNo(treeSearchEntity.getPageIndex());
        paginationInfo.setRecordCountPerPage(treeSearchEntity.getPageUnit());
        paginationInfo.setPageSize(autoPageSize);

        treeSearchEntity.setFirstIndex(paginationInfo.getFirstRecordIndex());
        treeSearchEntity.setLastIndex(paginationInfo.getLastRecordIndex());
        treeSearchEntity.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

        treeSearchEntity.setOrder(Order.desc("c_left"));
        List<T> list = treeDao.getList(treeSearchEntity);
        list.stream().forEach(data -> data.getPaginationInfo().setTotalRecordCount(totalCount));
        return list;
    }

    @Override
    public <T extends TreeSearchEntity> List<T> getNodesWithoutRoot(T treeSearchEntity) throws Exception {

        treeSearchEntity.setOrder(Order.desc("c_id"));
        Criterion criterion = Restrictions.not(
                // replace "id" below with property name, depending on what you're filtering against
                Restrictions.in("c_id", new Object[] {new Long(1), new Long(2)})
        );
        treeSearchEntity.getCriterions().add(criterion);
        List<T> list = getChildNode(treeSearchEntity);

        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TreeSearchEntity> List<String> searchNode(T treeSearchEntity) throws Exception {
        treeDao.setClazz(treeSearchEntity.getClass());
        treeDao.getCurrentSession().setCacheMode(CacheMode.IGNORE);
        treeSearchEntity.setOrder(Order.asc("c_id"));
        List<T> collectionObjects = treeDao.getList(treeSearchEntity);
        List<String> returnList = new ArrayList<String>();
        for (T rowObject : collectionObjects) {
            String rowData = "#node_" + rowObject.getC_id();
            returnList.add(rowData);
        }
        return returnList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = {Exception.class}, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public <T extends TreeSearchEntity> T addNode(T treeSearchEntity) throws Exception {

        treeDao.setClazz(treeSearchEntity.getClass());
        treeDao.getCurrentSession().setCacheMode(CacheMode.IGNORE);

        if (treeSearchEntity.getRef() < 0) {
            throw new RuntimeException("ref is minus");
        } else {

            T nodeByRef = (T) treeDao.getUnique(treeSearchEntity.getRef());

            if ("default".equals(nodeByRef.getC_type())) {
                throw new RuntimeException("nodeByRef is default Type");
            }

            nodeByRef.setWhere("c_parentid", nodeByRef.getC_id());
            final long lastPosiotionOfNodeByRef = treeDao.getCount(nodeByRef);

            treeSearchEntity.setC_position(lastPosiotionOfNodeByRef);

            long rightPointFromNodeByRef = nodeByRef.getC_right();
            rightPointFromNodeByRef = Math.max(rightPointFromNodeByRef, 1);

            long spaceOfTargetNode = 2;

            this.stretchLeftRightForMyselfFromTree(spaceOfTargetNode, rightPointFromNodeByRef,
                    treeSearchEntity.getCopy(), null, treeSearchEntity);

            long targetNodeLevel = treeSearchEntity.getRef() == 0 ? 0 : nodeByRef.getC_level() + 1;

            treeSearchEntity.setC_parentid(treeSearchEntity.getRef());
            treeSearchEntity.setC_left(rightPointFromNodeByRef);
            treeSearchEntity.setC_right(rightPointFromNodeByRef + 1);
            treeSearchEntity.setC_level(targetNodeLevel);

            long insertSeqResult = (long) treeDao.insert(treeSearchEntity);
            if (insertSeqResult > 0) {
                final long SUCCESS = 1;
                treeSearchEntity.setStatus(SUCCESS);
                treeSearchEntity.setId(insertSeqResult);
            } else {
                throw new RuntimeException("심각한 오류 발생 - 삽입 노드");
            }
        }
        return treeSearchEntity;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class}, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public <T extends TreeSearchEntity> int overwriteNode( T toEntity ,T fromEntity) throws Exception {

        treeDao.setClazz(toEntity.getClass());
        treeDao.getCurrentSession().setCacheMode(CacheMode.IGNORE);
        T alterTargetNode = (T) treeDao.getUnique(toEntity.getC_id());

        for (Field toField : ReflectionUtils.getAllFields(toEntity.getClass())) {

            toField.setAccessible(true);

            Field fromField = ReflectionUtils.getFieldWithName(fromEntity.getClass(),
                    toField.getName(), false);

            fromField.setAccessible(true);

            Object fromValue = fromField.get(fromEntity);

            if (!ObjectUtils.isEmpty(fromValue)) {
                toField.setAccessible(true);
                toField.set(alterTargetNode, fromValue);
            }

        }
        treeDao.update(alterTargetNode);

        return 1;

    }

    @SuppressWarnings("unchecked")
    public <T extends TreeSearchEntity> void stretchLeftRightForMyselfFromTree(long spaceOfTargetNode,
                                                                            long rightPositionFromNodeByRef, long copy, Collection<Long> c_idsByChildNodeFromNodeById,
                                                                            T treeSearchEntity) throws Exception {

        DetachedCriteria detachedLeftCriteria = DetachedCriteria.forClass(treeSearchEntity.getClass());
        stretchLeft(spaceOfTargetNode, rightPositionFromNodeByRef, copy, c_idsByChildNodeFromNodeById,
                detachedLeftCriteria);
        DetachedCriteria detachedRightCriteria = DetachedCriteria.forClass(treeSearchEntity.getClass());
        stretchRight(spaceOfTargetNode, rightPositionFromNodeByRef, copy, c_idsByChildNodeFromNodeById,
                detachedRightCriteria);
    }

    @SuppressWarnings("unchecked")
    public <T extends TreeSearchEntity> void stretchRight(long spaceOfTargetNode,
                                                       long rightPositionFromNodeByRef, long copy, Collection<Long> c_idsByChildNodeFromNodeById,
                                                       DetachedCriteria detachedCriteria) {
        logger.debug("-----------------------stretchRight 완료-----------------------");
        Criterion where = Restrictions.ge("c_right", rightPositionFromNodeByRef);
        detachedCriteria.add(where);
        if (copy == 0) {
            if (c_idsByChildNodeFromNodeById != null && c_idsByChildNodeFromNodeById.size() > 0) {
                detachedCriteria.add(Restrictions.and(Restrictions.not(Restrictions.in("c_id",
                        c_idsByChildNodeFromNodeById))));
            }
        }
        detachedCriteria.addOrder(Order.asc("c_id"));
        List<T> updateTargetList = treeDao.getListWithoutPaging(detachedCriteria);

        for (T perTreeSearchEntity : updateTargetList) {
            perTreeSearchEntity.setC_right(perTreeSearchEntity.getC_right() + spaceOfTargetNode);
            try {
                treeDao.update(perTreeSearchEntity);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends TreeSearchEntity> void stretchLeft(long spaceOfTargetNode,
                                                      long rightPositionFromNodeByRef, long copy, Collection<Long> c_idsByChildNodeFromNodeById,
                                                      DetachedCriteria detachedCriteria) {

        logger.debug("-----------------------stretchLeft 완료-----------------------");
        Criterion where = Restrictions.ge("c_left", rightPositionFromNodeByRef);
        detachedCriteria.add(where);
        if (copy == 0) {
            if (c_idsByChildNodeFromNodeById != null && c_idsByChildNodeFromNodeById.size() > 0) {
                detachedCriteria.add(Restrictions.and(Restrictions.not(Restrictions.in("c_id",
                        c_idsByChildNodeFromNodeById))));
            }
        }
        detachedCriteria.addOrder(Order.asc("c_id"));
        List<T> updateTargetList = treeDao.getListWithoutPaging(detachedCriteria);

        for (T perTreeSearchEntity : updateTargetList) {
            perTreeSearchEntity.setC_left(perTreeSearchEntity.getC_left() + spaceOfTargetNode);
            try {
                treeDao.update(perTreeSearchEntity);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 파라미터로 넘겨진 인스턴스의 정보를 이용해 리플렉션하여 새로운 인스턴스를 만들어 반환한다.
     * 리플렉션을 위한 타입 정보를 제공하기 위한 인스턴스
     *
     * @param treeSearchEntity
     * @return T extends TreeSearchEntity
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public <T extends TreeSearchEntity> T newInstance(T treeSearchEntity) throws Exception {
        Class<T> target = (Class<T>) Class.forName(treeSearchEntity.getClass().getCanonicalName());
        return target.newInstance();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = {Exception.class}, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public <T extends TreeSearchEntity> int removeNode(T treeSearchEntity) throws Exception {

        treeDao.setClazz(treeSearchEntity.getClass());
        treeDao.getCurrentSession().setCacheMode(CacheMode.IGNORE);
        Criterion whereGetNode = Restrictions.eq("c_id", treeSearchEntity.getC_id());
        T removeNode = (T) treeDao.getUnique(whereGetNode);

        long spaceOfTargetNode = removeNode.getC_right() - removeNode.getC_left() + 1;

        removeNode.setSpaceOfTargetNode(spaceOfTargetNode);

        DetachedCriteria detachedDeleteCriteria = DetachedCriteria.forClass(treeSearchEntity.getClass());
        Criterion where = Restrictions.ge("c_left", removeNode.getC_left());
        detachedDeleteCriteria.add(where);
        detachedDeleteCriteria.add(Restrictions.and(Restrictions.le("c_right", removeNode.getC_right())));
        detachedDeleteCriteria.addOrder(Order.asc("c_id"));
        try {
            List<T> deleteTargetList = treeDao.getListWithoutPaging(detachedDeleteCriteria);
            for (T deleteTreeSearchEntity : deleteTargetList) {
                treeDao.delete(deleteTreeSearchEntity);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        DetachedCriteria detachedRemovedAfterLeftFixCriteria = DetachedCriteria.forClass(treeSearchEntity.getClass());
        Criterion whereRemovedAfterLeftFix = Restrictions.gt("c_left", removeNode.getC_right());
        detachedRemovedAfterLeftFixCriteria.add(whereRemovedAfterLeftFix);
        detachedRemovedAfterLeftFixCriteria.addOrder(Order.asc("c_id"));
        List<T> updateRemovedAfterLeftFixtList = treeDao
                .getListWithoutPaging(detachedRemovedAfterLeftFixCriteria);
        for (T perLeftFixTreeSearchEntity : updateRemovedAfterLeftFixtList) {
            perLeftFixTreeSearchEntity.setC_left(perLeftFixTreeSearchEntity.getC_left() - spaceOfTargetNode);
            treeDao.update(perLeftFixTreeSearchEntity);
        }

        DetachedCriteria detachedRemovedAfterRightFixCriteria = DetachedCriteria
                .forClass(treeSearchEntity.getClass());
        Criterion whereRemovedAfterRightFix = Restrictions.gt("c_right", removeNode.getC_left());
        detachedRemovedAfterRightFixCriteria.add(whereRemovedAfterRightFix);
        detachedRemovedAfterRightFixCriteria.addOrder(Order.asc("c_id"));
        List<T> updateRemovedAfterRightFixtList = treeDao
                .getListWithoutPaging(detachedRemovedAfterRightFixCriteria);
        for (T perRightFixTreeSearchEntity : updateRemovedAfterRightFixtList) {
            perRightFixTreeSearchEntity.setC_right(perRightFixTreeSearchEntity.getC_right() - spaceOfTargetNode);
            treeDao.update(perRightFixTreeSearchEntity);
        }

        DetachedCriteria detachedRemovedAfterPositionFixCriteria = DetachedCriteria.forClass(treeSearchEntity
                .getClass());
        Criterion whereRemovedAfterPositionFix = Restrictions.eq("c_parentid", removeNode.getC_parentid());
        detachedRemovedAfterPositionFixCriteria.add(whereRemovedAfterPositionFix);
        detachedRemovedAfterPositionFixCriteria.add(Restrictions.and(Restrictions.gt("c_position",
                removeNode.getC_position())));
        detachedRemovedAfterPositionFixCriteria.addOrder(Order.asc("c_id"));
        List<T> updateRemovedAfterPositionFixtList = treeDao
                .getListWithoutPaging(detachedRemovedAfterPositionFixCriteria);
        for (T perPositionFixTreeSearchEntity : updateRemovedAfterPositionFixtList) {
            perPositionFixTreeSearchEntity.setC_position(perPositionFixTreeSearchEntity.getC_position() - 1);
            treeDao.update(perPositionFixTreeSearchEntity);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = {Exception.class}, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public <T extends TreeSearchEntity> int updateNode(T treeSearchEntity) throws Exception {

        treeDao.setClazz(treeSearchEntity.getClass());
        treeDao.getCurrentSession().setCacheMode(CacheMode.IGNORE);
        T alterTargetNode = (T) treeDao.getUnique(treeSearchEntity.getC_id());

        for (Field field : ReflectionUtils.getAllFields(treeSearchEntity.getClass())) {

            field.setAccessible(true);

            Object value = field.get(treeSearchEntity);

            if (!ObjectUtils.isEmpty(value)) {
                field.setAccessible(true);
                field.set(alterTargetNode, value);
            }

        }
        treeDao.update(alterTargetNode);

        return 1;

    }

    @Transactional(rollbackFor = {Exception.class}, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public <T extends TreeSearchEntity> List<T> saveOrUpdateList(List<T> entities) throws Exception {

        List<T> resultHostEntities = new ArrayList<>();

        for (T entity : entities) {
            T oneBy = this.getNode(entity);
            if(oneBy==null){
                entity.setRef(2L);
                entity.setC_type("default");
                this.addNode(entity);
            }else{
                this.overwriteNode(oneBy,entity);
            }
            resultHostEntities.add(oneBy);
        }
        return resultHostEntities;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = {Exception.class}, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public <T extends TreeSearchEntity> int alterNode(T treeSearchEntity) throws Exception {

        treeDao.setClazz(treeSearchEntity.getClass());
        treeDao.getCurrentSession().setCacheMode(CacheMode.IGNORE);
        T alterTargetNode = (T) treeDao.getUnique(treeSearchEntity.getC_id());
        alterTargetNode.setC_title(treeSearchEntity.getC_title());
        alterTargetNode.setFieldFromNewInstance(treeSearchEntity);
        treeDao.update(alterTargetNode);
        return 1;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = {Exception.class}, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public <T extends TreeSearchEntity> int alterNodeType(T treeSearchEntity) throws Exception {

        treeDao.setClazz(treeSearchEntity.getClass());
        treeDao.getCurrentSession().setCacheMode(CacheMode.IGNORE);
        T nodeById = (T) treeDao.getUnique(treeSearchEntity.getC_id());

        if (nodeById.getC_type().equals(treeSearchEntity.getC_type())) {
            return 1;
        } else if ("default".equals(treeSearchEntity.getC_type())) {
            nodeById.setWhere("c_parentid", nodeById.getC_id());
            List<T> childNodesFromNodeById = treeDao.getList(nodeById);
            if (childNodesFromNodeById.size() != 0) {
                throw new RuntimeException("하위에 노드가 있는데 디폴트로 바꾸려고 함");
            } else {
                nodeById.setC_type(treeSearchEntity.getC_type());
                treeDao.update(nodeById);
            }
        } else if ("folder".equals(treeSearchEntity.getC_type())) {
            nodeById.setC_type(treeSearchEntity.getC_type());
            treeDao.update(nodeById);
            return 1;
        }
        return 1;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = {Exception.class}, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public <T extends TreeSearchEntity> T moveNode(T treeSearchEntity, HttpServletRequest request)
            throws Exception {

        treeDao.setClazz(treeSearchEntity.getClass());
        treeDao.getCurrentSession().setCacheMode(CacheMode.IGNORE);

        logger.debug("***********************MoveNode***********************");
        logger.debug("-----------------------getNode 완료-----------------------");

        T nodeById = getNode(treeSearchEntity);
        if (nodeById == null) {
            throw new RuntimeException("nodeById is null");
        }
        Long nodeByIdLeft = nodeById.getC_left();

        logger.debug("-----------------------getChildNodeByLeftRight 완료-----------------------");
        DetachedCriteria getChildNodeByLeftRightCriteria = DetachedCriteria.forClass(treeSearchEntity.getClass());

        Criterion criterion = Restrictions.and(
                Restrictions.ge("c_left", nodeById.getC_left()),
                Restrictions.le("c_right", nodeById.getC_right())
        );
        getChildNodeByLeftRightCriteria.add(criterion);
        getChildNodeByLeftRightCriteria.addOrder(Order.asc("c_left"));
        List<T> childNodesFromNodeById = treeDao.getListWithoutPaging(getChildNodeByLeftRightCriteria);

        logger.debug("-----------------------position 값이 over될때 방어코드-----------------------");
        DetachedCriteria getChildNodeByPositionCriteria = DetachedCriteria.forClass(treeSearchEntity.getClass());

        Criterion postion_criterion = Restrictions.eq("c_parentid", treeSearchEntity.getRef());
        getChildNodeByPositionCriteria.add(postion_criterion);
        int refChildCount = treeDao.getListWithoutPaging(getChildNodeByPositionCriteria).size();
        if (treeSearchEntity.getC_position() > refChildCount) {
            treeSearchEntity.setC_position(Long.valueOf(refChildCount));
        }

        logger.debug("-----------------------nodeByRef 완료-----------------------");
        T nodeByRef = (T) treeDao.getUnique(treeSearchEntity.getRef());
        if (StringUtils.equals(nodeByRef.getC_type(), "default")) {
            throw new RuntimeException("ref is not default type");
        }
        long rightPointFromNodeByRef = nodeByRef.getC_right();

        logger.debug("-----------------------childNodesFromNodeByRef 완료-----------------------");
        DetachedCriteria getNodeByRefCriteria = DetachedCriteria.forClass(treeSearchEntity.getClass());
        Criterion whereNodeByRef = Restrictions.eq("c_parentid", nodeByRef.getC_id());
        getNodeByRefCriteria.add(whereNodeByRef);
        List<T> childNodesFromNodeByRef = (List<T>) treeDao.getListWithoutPaging(getNodeByRefCriteria);

        T t_ComprehensiveTree = newInstance(treeSearchEntity);

        long spaceOfTargetNode = 2;
        Collection<Long> c_idsByChildNodeFromNodeById = null;

        logger.debug("-----------------------c_idsByChildNodeFromNodeById 완료-----------------------");
        c_idsByChildNodeFromNodeById = CollectionUtils.collect(childNodesFromNodeById, new Transformer<T, Long>() {
            @Override
            public Long transform(T childNodePerNodeById) {
                return childNodePerNodeById.getC_id();
            }
        });

        if (c_idsByChildNodeFromNodeById.contains(treeSearchEntity.getRef())) {
            throw new RuntimeException("myself contains already refTargetNode");
        }

        spaceOfTargetNode = nodeById.getC_right() - nodeById.getC_left() + 1;

        if (!treeSearchEntity.isCopied()) {
            logger.debug("-----------------------cutMyself 완료-----------------------");
            this.cutMyself(nodeById, spaceOfTargetNode, c_idsByChildNodeFromNodeById);
        }

        logger.debug("-----------------------calculatePostion 완료-----------------------");

        //bug fix: 세션 값이 유지되므로, 구분자를 줘야 하는 문제를 테이블 명으로 잡았음.
        Table table = treeSearchEntity.getClass().getAnnotation(Table.class);
        String tableName = table.name();

        tableName = RouteTableInterceptor.setReplaceTableName(request, tableName);
        this.calculatePostion(treeSearchEntity, nodeById, childNodesFromNodeByRef, request, tableName);

        if (rightPointFromNodeByRef < 1) {
            rightPointFromNodeByRef = 1;
        }

        if (!treeSearchEntity.isCopied()) {
            logger.debug("-----------------------stretchPositionForMyselfFromTree 완료-----------------------");
            this.stretchPositionForMyselfFromTree(c_idsByChildNodeFromNodeById, treeSearchEntity);

            int selfPosition = (nodeById.getC_parentid() == treeSearchEntity.getRef() && treeSearchEntity
                    .getC_position() > nodeById.getC_position()) ? 1 : 0;

            for (T child : childNodesFromNodeByRef) {
                if (child.getC_position() - selfPosition == treeSearchEntity.getC_position()) {
                    rightPointFromNodeByRef = child.getC_left();
                    break;
                }
            }

            if (nodeById.getC_left() < rightPointFromNodeByRef) {
                rightPointFromNodeByRef -= spaceOfTargetNode;
            }
        }

        logger.debug("-----------------------stretchLeftRightForMyselfFromTree 완료-----------------------");
        this.stretchLeftRightForMyselfFromTree(spaceOfTargetNode, rightPointFromNodeByRef,
                treeSearchEntity.getCopy(), c_idsByChildNodeFromNodeById, treeSearchEntity);

        if (logger.isDebugEnabled()) {
            logger.debug(">>>>>>>>>>>>>>>>>>>>" + rightPointFromNodeByRef);
        }

        long targetNodeLevel = nodeById.getC_level() - (nodeByRef.getC_level() + 1);
        long comparePoint = nodeByIdLeft - rightPointFromNodeByRef;

        if (logger.isDebugEnabled()) {
            logger.debug(">>>>>>>>>>>>>>>>>>>>" + comparePoint);
        }

        if (treeSearchEntity.isCopied()) {
            logger.debug("-----------------------pasteMyselfFromTree 완료-----------------------");
            long insertSeqResult = this
                    .pasteMyselfFromTree(treeSearchEntity.getRef(), comparePoint, spaceOfTargetNode,
                            targetNodeLevel, c_idsByChildNodeFromNodeById, rightPointFromNodeByRef, nodeById);
            t_ComprehensiveTree.setId(insertSeqResult);
            logger.debug("-----------------------fixPositionParentIdOfCopyNodes-----------------------");
            this.fixPositionParentIdOfCopyNodes(insertSeqResult, treeSearchEntity.getC_position(), treeSearchEntity);
        } else {
            logger.debug("-----------------------enterMyselfFromTree 완료-----------------------");
            this.enterMyselfFromTree(treeSearchEntity.getRef(), treeSearchEntity.getC_position(),
                    treeSearchEntity.getC_id(), comparePoint, targetNodeLevel, c_idsByChildNodeFromNodeById,
                    treeSearchEntity);
            enterMyselfFixLeftRight(comparePoint, targetNodeLevel, c_idsByChildNodeFromNodeById, treeSearchEntity);
        }

        return t_ComprehensiveTree;
    }

    @SuppressWarnings("unchecked")
    public <T extends TreeSearchEntity> void enterMyselfFromTree(long ref, long c_position, long c_id,
                                                              long idif, long ldif, Collection<Long> c_idsByChildNodeFromNodeById, T treeSearchEntity) throws Exception {

        treeDao.setClazz(treeSearchEntity.getClass());
        logger.debug("-----------------------enterMyselfFixPosition-----------------------");

        T childEnterMyselfFixPosition = (T) treeDao.getUnique(treeSearchEntity.getC_id());
        childEnterMyselfFixPosition.setC_parentid(ref);
        childEnterMyselfFixPosition.setC_position(c_position);
        treeDao.update(childEnterMyselfFixPosition);

    }

    @SuppressWarnings("unchecked")
    public <T extends TreeSearchEntity> void enterMyselfFixLeftRight(long idif, long ldif,
                                                                  Collection<Long> c_idsByChildNodeFromNodeById, T treeSearchEntity) {
        logger.debug("-----------------------enterMyselfFixLeftRight-----------------------");
        DetachedCriteria detachedEnterMyselfFixLeftRightCriteria = DetachedCriteria.forClass(treeSearchEntity
                .getClass());
        if (c_idsByChildNodeFromNodeById != null && c_idsByChildNodeFromNodeById.size() > 0) {
            Criterion whereEnterMyselfFixLeftRight = Restrictions.in("c_id", c_idsByChildNodeFromNodeById);
            detachedEnterMyselfFixLeftRightCriteria.add(whereEnterMyselfFixLeftRight);
            detachedEnterMyselfFixLeftRightCriteria.addOrder(Order.asc("c_id"));

            List<T> enterMyselfFixLeftRightList = treeDao
                    .getListWithoutPaging(detachedEnterMyselfFixLeftRightCriteria);
            for (T perEnterMyselfFixLeftRightList : enterMyselfFixLeftRightList) {
                logger.debug(perEnterMyselfFixLeftRightList.toString());
                perEnterMyselfFixLeftRightList.setC_left(perEnterMyselfFixLeftRightList.getC_left() - idif);
                perEnterMyselfFixLeftRightList.setC_right(perEnterMyselfFixLeftRightList.getC_right() - idif);
                perEnterMyselfFixLeftRightList.setC_level(perEnterMyselfFixLeftRightList.getC_level() - ldif);
                treeDao.update(perEnterMyselfFixLeftRightList);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends TreeSearchEntity> void fixPositionParentIdOfCopyNodes(long insertSeqResult,
                                                                         long position, T treeSearchEntity) throws Exception {

        treeDao.setClazz(treeSearchEntity.getClass());

        T node = (T) treeDao.getUnique(insertSeqResult);

        logger.debug("-----------------------fixPositionParentIdOfCopyNodes 완료-----------------------");
        DetachedCriteria getChildNodeByLeftRightCriteria = DetachedCriteria.forClass(treeSearchEntity.getClass());
        Criterion whereChildNodeByLeftRight = Restrictions.ge("c_left", node.getC_left());
        getChildNodeByLeftRightCriteria.add(whereChildNodeByLeftRight);
        getChildNodeByLeftRightCriteria.add(Restrictions.and(Restrictions.le("c_right", node.getC_right())));
        getChildNodeByLeftRightCriteria.addOrder(Order.asc("c_left"));
        List<T> children = treeDao.getListWithoutPaging(getChildNodeByLeftRightCriteria);

        Map<Long, Long> parentIds = new HashMap<Long, Long>();

        for (T child : children) {
            for (long i = child.getC_left() + 1; i < child.getC_right(); i++) {
                long parentId = child.getC_id();
                parentIds.put(i, parentId);
            }

            if (child.getC_id() == insertSeqResult) {
                if (logger.isDebugEnabled()) {
                    logger.debug(">>>>>>>>>>>>>>>>> 기준노드가 잡혔음.");
                    logger.debug("C_TITLE    = " + child.getC_title());
                    logger.debug("C_ID       = " + insertSeqResult);
                    logger.debug("C_POSITION = " + position);
                }

                node.setC_position(position);

                treeDao.update(node);
                continue;
            }

            if (logger.isDebugEnabled()) {
                logger.debug(">>>>>>>>>>>>>>>>> 기준노드 아래 있는 녀석임");
                logger.debug("C_TITLE    = " + child.getC_title());
                logger.debug("C_ID       = " + child.getC_id());
                logger.debug("C_POSITION = " + child.getC_position());
                logger.debug("C_PARENTID = " + child.getC_parentid());
                logger.debug("부모아이디값 = " + parentIds.get(child.getC_left()));
            }

            child.setFixCopyId(parentIds.get(child.getC_left()));
            child.setC_parentid(parentIds.get(child.getC_left()));
            treeDao.update(child);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends TreeSearchEntity> long pasteMyselfFromTree(long ref, long idif,
                                                              long spaceOfTargetNode, long ldif, Collection<Long> c_idsByChildNodeFromNodeById,
                                                              long rightPositionFromNodeByRef, T nodeById) throws Exception {

        treeDao.setClazz(nodeById.getClass());

        T onlyPasteMyselfFromTree = getNode(nodeById);

        onlyPasteMyselfFromTree.setRef(ref);
        onlyPasteMyselfFromTree.setIdif(idif);
        onlyPasteMyselfFromTree.setSpaceOfTargetNode(spaceOfTargetNode);
        onlyPasteMyselfFromTree.setLdif(ldif);
        onlyPasteMyselfFromTree.setC_idsByChildNodeFromNodeById(c_idsByChildNodeFromNodeById);
        onlyPasteMyselfFromTree.setRightPositionFromNodeByRef(rightPositionFromNodeByRef);
        onlyPasteMyselfFromTree.setNodeById(nodeById);

        onlyPasteMyselfFromTree.setIdifLeft(idif
                + (nodeById.getC_left() >= rightPositionFromNodeByRef ? spaceOfTargetNode : 0));
        onlyPasteMyselfFromTree.setIdifRight(idif
                + (nodeById.getC_left() >= rightPositionFromNodeByRef ? spaceOfTargetNode : 0));

        DetachedCriteria detachedPasteMyselfFromTreeCriteria = DetachedCriteria.forClass(nodeById
                .getClass());
        if (c_idsByChildNodeFromNodeById != null && c_idsByChildNodeFromNodeById.size() > 0) {
            Criterion wherePasteMyselfFromTree = Restrictions.in("c_id", c_idsByChildNodeFromNodeById);
            detachedPasteMyselfFromTreeCriteria.add(wherePasteMyselfFromTree);
            detachedPasteMyselfFromTreeCriteria.addOrder(Order.desc("c_level"));

            List<T> pasteMyselfFromTreeList = treeDao
                    .getListWithoutPaging(detachedPasteMyselfFromTreeCriteria);
            for (T perPasteMyselfFromTree : pasteMyselfFromTreeList) {
                logger.debug("------pasteMyselfFromTree------LOOP---" + perPasteMyselfFromTree.getC_id());
                T addTarget = newInstance(perPasteMyselfFromTree);

                addTarget.setC_parentid(onlyPasteMyselfFromTree.getRef());
                addTarget.setC_position(perPasteMyselfFromTree.getC_position());
                addTarget.setC_left(perPasteMyselfFromTree.getC_left() - onlyPasteMyselfFromTree.getIdifLeft());
                addTarget.setC_right(perPasteMyselfFromTree.getC_right() - onlyPasteMyselfFromTree.getIdifRight());
                addTarget.setC_level(perPasteMyselfFromTree.getC_level() - onlyPasteMyselfFromTree.getLdif());
                addTarget.setC_title(perPasteMyselfFromTree.getC_title());
                addTarget.setC_type(perPasteMyselfFromTree.getC_type());

                addTarget.setFieldFromNewInstance(perPasteMyselfFromTree);
                logger.debug("여기에 추가적으로 확장한 필드에 대한 함수가 들어가야 한다 패턴을 쓰자");

                long insertSeqResult = (long) treeDao.insert(addTarget);
                perPasteMyselfFromTree.setId(insertSeqResult);

                if (insertSeqResult > 0) {
                    return insertSeqResult;
                } else {
                    throw new RuntimeException("심각한 오류 발생 - 삽입 노드");
                }
            }
        }

        return 0;
    }

    @SuppressWarnings("unchecked")
    public <T extends TreeSearchEntity> void stretchPositionForMyselfFromTree(
            Collection<Long> c_idsByChildNodeFromNodeById, T treeSearchEntity) throws Exception {

        treeDao.setClazz(treeSearchEntity.getClass());
        treeSearchEntity.setC_idsByChildNodeFromNodeById(c_idsByChildNodeFromNodeById);

        DetachedCriteria detachedStretchPositionForMyselfCriteria = DetachedCriteria.forClass(treeSearchEntity
                .getClass());
        Criterion whereStretchPositionForMyself = Restrictions.eq("c_parentid", treeSearchEntity.getRef());
        detachedStretchPositionForMyselfCriteria.add(whereStretchPositionForMyself);
        detachedStretchPositionForMyselfCriteria.add(Restrictions.and(Restrictions.ge("c_position",
                treeSearchEntity.getC_position())));
        if (treeSearchEntity.getCopy() == 0) {
            if (c_idsByChildNodeFromNodeById != null && c_idsByChildNodeFromNodeById.size() > 0) {
                detachedStretchPositionForMyselfCriteria.add(Restrictions.and(Restrictions.not(Restrictions.in("c_id",
                        c_idsByChildNodeFromNodeById))));
            }
        }
        detachedStretchPositionForMyselfCriteria.addOrder(Order.asc("c_id"));

        List<T> stretchPositionForMyselfList = treeDao
                .getListWithoutPaging(detachedStretchPositionForMyselfCriteria);
        for (T perStretchPositionForMyself : stretchPositionForMyselfList) {
            perStretchPositionForMyself.setC_position(perStretchPositionForMyself.getC_position() + 1);
            treeDao.update(perStretchPositionForMyself);
        }

    }

    public <T extends TreeSearchEntity> void calculatePostion(T treeSearchEntity, T nodeById,
                                                              List<T> childNodesFromNodeByRef, HttpServletRequest request, String tableName) throws Exception {
        HttpSession session = request.getSession();

        final boolean isMoveNodeInMyParent = (treeSearchEntity.getRef() == nodeById.getC_parentid());
        final boolean isMultiCounterZero = (treeSearchEntity.getMultiCounter() == 0);
        final boolean isBeyondTheCurrentToMoveNodes = (treeSearchEntity.getC_position() > nodeById.getC_position());

        if (isMoveNodeInMyParent) {
            if (logger.isDebugEnabled()) {
                logger.debug(">>>>>>>>>>>>>>>이동할 노드가 내 부모안에서 움직일때");
            }

            if (isMultiCounterZero) {
                if (isBeyondTheCurrentToMoveNodes) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(">>>>>>>>>>>>>>>이동 할 노드가 현재보다 뒤일때");
                        logger.debug("노드값=" + nodeById.getC_title());
                        logger.debug("노드의 초기 위치값=" + nodeById.getC_position());
                        logger.debug("노드의 요청받은 위치값=" + treeSearchEntity.getC_position());
                        logger.debug("노드의 요청받은 멀티카운터=" + treeSearchEntity.getMultiCounter());
                    }

                    final boolean isFolderToMoveNodes = (treeSearchEntity.getC_position() > childNodesFromNodeByRef
                            .size());

                    if (isFolderToMoveNodes) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("노드 이동시 폴더를 대상으로 했을때 생기는 버그 발생 =" + treeSearchEntity.getC_position());
                        }
                        long childNodesFromNodeByRefCnt = childNodesFromNodeByRef.size();
                        treeSearchEntity.setC_position(childNodesFromNodeByRefCnt);
                    } else {
                        treeSearchEntity.setC_position(treeSearchEntity.getC_position() - 1);
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("노드의 최종 위치값=" + treeSearchEntity.getC_position());
                }
                session.setAttribute(tableName + "_settedPosition", treeSearchEntity.getC_position());
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug(">>>>>>>>>>>>>>>멀티 카운터가 0 이 아닐때");
                    logger.debug("노드값=" + nodeById.getC_title());
                    logger.debug("노드의 초기 위치값=" + nodeById.getC_position());
                    logger.debug("노드의 요청받은 위치값=" + treeSearchEntity.getC_position());
                    logger.debug("노드의 요청받은 멀티카운터=" + treeSearchEntity.getMultiCounter());
                    logger.debug("0번 노드의 위치값=" + session.getAttribute(tableName + "_settedPosition"));
                }

                long increasePosition = 0;

                final boolean isMultiNodeOfPositionsAtZeroThanBehind = ((Integer) session
                        .getAttribute("settedPosition") < nodeById.getC_position());

                if (isMultiNodeOfPositionsAtZeroThanBehind) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(">>>>>>>>>>>>>>>멀티 노드의 위치가 0번 노드보다 뒤일때");
                    }

                    increasePosition = (Integer) session.getAttribute(tableName + "_settedPosition") + 1;
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug(">>>>>>>>>>>>>>>멀티 노드의 위치가 0번 노드보다 앞일때");
                    }

                    if (treeSearchEntity.isCopied()) {
                        increasePosition = (Integer) session.getAttribute(tableName + "_settedPosition") + 1;
                    } else {
                        increasePosition = (Integer) session.getAttribute(tableName + "_settedPosition");
                    }

                }
                session.setAttribute(tableName + "_settedPosition", increasePosition);

                treeSearchEntity.setC_position(increasePosition);

                final boolean isSamePosition = (nodeById.getC_position() == treeSearchEntity.getC_position());

                if (isSamePosition) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(">>>>>>>>>>>>>>>원래 노드 위치값과 최종 계산된 노드의 위치값이 동일한 경우");
                    }

                    session.setAttribute(tableName + "_settedPosition", increasePosition - 1);
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("노드의 최종 위치값=" + treeSearchEntity.getC_position());
                }
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(">>>>>>>>>>>>>>>이동할 노드가 내 부모밖으로 움직일때");
            }

            if (isMultiCounterZero) {
                if (logger.isDebugEnabled()) {
                    logger.debug(">>>>>>>>>>>>>>>멀티 카운터가 0 일때");
                    logger.debug("노드값=" + nodeById.getC_title());
                    logger.debug("노드의 초기 위치값=" + nodeById.getC_position());
                    logger.debug("노드의 요청받은 위치값=" + treeSearchEntity.getC_position());
                    logger.debug("노드의 요청받은 멀티카운터=" + treeSearchEntity.getMultiCounter());
                    logger.debug("노드의 최종 위치값=" + treeSearchEntity.getC_position());
                }

                session.setAttribute(tableName + "_settedPosition", treeSearchEntity.getC_position());
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug(">>>>>>>>>>>>>>>멀티 카운터가 0 이 아닐때");
                    logger.debug("노드값=" + nodeById.getC_title());
                    logger.debug("노드의 초기 위치값=" + nodeById.getC_position());
                    logger.debug("노드의 요청받은 위치값=" + treeSearchEntity.getC_position());
                    logger.debug("노드의 요청받은 멀티카운터=" + treeSearchEntity.getMultiCounter());
                }

                long increasePosition = 0;
                increasePosition = NumberUtils.toLong(session.getAttribute(tableName + "_settedPosition").toString()) + 1;
                treeSearchEntity.setC_position(increasePosition);
                session.setAttribute(tableName + "_settedPosition", increasePosition);

                if (logger.isDebugEnabled()) {
                    logger.debug("노드의 최종 위치값=" + treeSearchEntity.getC_position());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends TreeSearchEntity> void cutMyself(T nodeById, long spaceOfTargetNode,
                                                    Collection<Long> c_idsByChildNodeFromNodeById) throws Exception {

        treeDao.setClazz(nodeById.getClass());
        nodeById.setSpaceOfTargetNode(spaceOfTargetNode);
        nodeById.setC_idsByChildNodeFromNodeById(c_idsByChildNodeFromNodeById);

        logger.debug("***********************CutMyself***********************");
        logger.debug("-----------------------cutMyselfPositionFix-----------------------");
        DetachedCriteria cutMyselfPositionFixCriteria = DetachedCriteria.forClass(nodeById.getClass());
        Criterion whereCutMyselfPositionFix = Restrictions.eq("c_parentid", nodeById.getC_parentid());
        cutMyselfPositionFixCriteria.add(whereCutMyselfPositionFix);
        cutMyselfPositionFixCriteria.add(Restrictions.and(Restrictions.gt("c_position", nodeById.getC_position())));
        cutMyselfPositionFixCriteria.addOrder(Order.asc("c_id"));
        List<T> childCutMyselfPositionFix = treeDao.getListWithoutPaging(cutMyselfPositionFixCriteria);
        for (T perNodeById : childCutMyselfPositionFix) {
            perNodeById.setC_position(perNodeById.getC_position() - 1);
            treeDao.update(perNodeById);
        }

        logger.debug("-----------------------cutMyselfLeftFix-----------------------");
        DetachedCriteria cutMyselfLeftFixCriteria = DetachedCriteria.forClass(nodeById.getClass());
        Criterion whereCutMyselfLeftFix = Restrictions.gt("c_left", nodeById.getC_right());
        cutMyselfLeftFixCriteria.add(whereCutMyselfLeftFix);
        cutMyselfLeftFixCriteria.addOrder(Order.asc("c_id"));
        List<T> childCutMyselfLeftFix = treeDao.getListWithoutPaging(cutMyselfLeftFixCriteria);
        for (T perCutMyselfLeftFix : childCutMyselfLeftFix) {
            perCutMyselfLeftFix.setC_left(perCutMyselfLeftFix.getC_left() - spaceOfTargetNode);
            treeDao.update(perCutMyselfLeftFix);
        }

        logger.debug("-----------------------cutMyselfRightFix-----------------------");
        DetachedCriteria cutMyselfRightFixCriteria = DetachedCriteria.forClass(nodeById.getClass());
        Criterion whereCutMyselfRightFix = Restrictions.gt("c_right", nodeById.getC_left());
        cutMyselfRightFixCriteria.add(whereCutMyselfRightFix);
        if (c_idsByChildNodeFromNodeById != null && c_idsByChildNodeFromNodeById.size() > 0) {
            cutMyselfRightFixCriteria.add(Restrictions.and(Restrictions.not(Restrictions.in("c_id",
                    c_idsByChildNodeFromNodeById))));
        }
        cutMyselfRightFixCriteria.addOrder(Order.asc("c_id"));
        List<T> childCutMyselfRightFix = treeDao.getListWithoutPaging(cutMyselfRightFixCriteria);
        for (T perCutMyselfRightFix : childCutMyselfRightFix) {
            perCutMyselfRightFix.setC_right(perCutMyselfRightFix.getC_right() - spaceOfTargetNode);
            treeDao.update(perCutMyselfRightFix);
        }

    }

}
