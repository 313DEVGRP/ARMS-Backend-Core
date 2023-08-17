package com.arms.jira.jiraissuepriority.model;

import com.egovframework.javaservice.treeframework.model.TreeBaseDTO;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Lob;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JiraIssuePriorityDTO extends TreeBaseDTO {


    private String c_issue_priority_id;

    private String c_issue_priority_desc;

    private String c_issue_priority_name;

    private String c_issue_priority_url;

    //내용
    private String c_contents;

    //설명
    private String c_desc;

    //클라우드 대응 : private boolean isDefault;
    //비고
    private String c_etc;

}
