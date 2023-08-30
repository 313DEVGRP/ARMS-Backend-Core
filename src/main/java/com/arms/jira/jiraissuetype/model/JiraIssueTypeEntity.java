/*
 * @author Dongmin.lee
 * @since 2023-03-26
 * @version 23.03.26
 * @see <pre>
 *  Copyright (C) 2007 by 313 DEV GRP, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by 313 developer group <313@313.co.kr>, December 2010
 * </pre>
 */
package com.arms.jira.jiraissuetype.model;

import com.arms.jira.jiraserver.model.JiraServerEntity;
import com.arms.jira.jiraserver.service.JiraServer;
import com.egovframework.javaservice.treeframework.model.TreeBaseEntity;
import com.egovframework.javaservice.treeframework.model.TreeSearchEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.net.URI;

@Entity
@Getter
@Setter
@Builder
@Table(name = "T_ARMS_JIRAISSUETYPE")
@SelectBeforeUpdate(value=true)
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class JiraIssueTypeEntity extends TreeSearchEntity implements Serializable {

 	@Override
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "c_id")
    public Long getC_id() {
        return super.getC_id();
    }
    
    //@Getter @Setter

    //온프라미스 대응 : private final Long id;
    //클라우드 대응 : private String id;
    @Column(name = "c_issue_type_id")
    @Type(type="text")
    private String c_issue_type_id;

    //온프라미스 대응 : private final String description;
    //클라우드 대응 : private String description;
    @Column(name = "c_issue_type_desc")
    @Type(type="text")
    private String c_issue_type_desc;

    //온프라미스 대응 : private final String name;
    //클라우드 대응 : private String name;
    @Column(name = "c_issue_type_name")
    @Type(type="text")
    private String c_issue_type_name;

    //온프라미스 대응 : private final URI self;
    //클라우드 대응 : private String self;
    @Column(name = "c_issue_type_url")
    @Type(type="text")
    private String c_issue_type_url;

    //값으로 : true, false 를 가질 수 있다.
    @Column(name = "c_check")
    private String c_check;

    //내용
    //온프라미스 대응 : private String type; // 표준 이슈 유형(standard), 하위 작업 이슈 유형(subtask)
    //클라우드 대응 : private Integer hierarchyLevel;
    @Lob
    @Column(name = "c_contents")
    private String c_contents;

    //설명
    //온프라미스 대응 : private Boolean subtask;
    //클라우드 대응 : private Boolean subtask;
    @Column(name = "c_desc")
    @Type(type="text")
    private String c_desc;

    //비고
    //온프라미스 대응 : private final URI iconUri;
    //클라우드 대응 : private String untranslatedName;
    @Column(name = "c_etc")
    private String c_etc;

    /*
     * Extend Bean Field
     */
	@JsonIgnore
    private Boolean copyBooleanValue;

    @Transient
	@ApiModelProperty(hidden = true)
    public Boolean getCopyBooleanValue() {
        copyBooleanValue = false;
        if (this.getCopy() == 0) {
            copyBooleanValue = false;
        } else {
            copyBooleanValue = true;
        }
        return copyBooleanValue;
    }

    public void setCopyBooleanValue(Boolean copyBooleanValue) {
        this.copyBooleanValue = copyBooleanValue;
    }

    @Override
    public <T extends TreeSearchEntity> void setFieldFromNewInstance(T paramInstance) {
        if( paramInstance instanceof TreeBaseEntity){
            if(paramInstance.isCopied()) {
                this.setC_title("copy_" + this.getC_title());
            }
        }
    }
}
