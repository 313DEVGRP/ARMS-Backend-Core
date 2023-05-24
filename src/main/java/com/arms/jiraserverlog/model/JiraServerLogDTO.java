package com.arms.jiraserverlog.model;

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
public class JiraServerLogDTO extends TreeBaseDTO {

    private String c_jira_server_contents;

    private String c_jira_server_etc;

    private String c_jira_server_base_url;

    private String c_jira_server_version;

    private String c_jira_server_build;

    private String c_jira_server_title;

}
