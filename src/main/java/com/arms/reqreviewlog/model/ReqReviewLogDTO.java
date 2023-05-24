package com.arms.reqreviewlog.model;

import com.egovframework.javaservice.treeframework.model.TreeBaseDTO;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReqReviewLogDTO extends TreeBaseDTO {


    private Long c_pdservice_link;

    private Long c_version_link;

    private Long c_req_link;

    private String c_review_sender;

    private String c_review_responder;

    private String c_review_creat_date;

    private String c_review_result_state;

    private String c_review_result_date;

}
