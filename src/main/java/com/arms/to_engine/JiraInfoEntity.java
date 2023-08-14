package com.arms.to_engine;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JiraInfoEntity {

    private String connectId;

    private String userId;

    private String passwordOrToken;

    private String uri;

    private String issueId;

    private String issueName;

    private String self;


}
