package com.arms.reqreview.model;

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
public class ReqReviewDTO extends TreeBaseDTO {


    private Long c_pdservice_link;

    private Long c_version_link;

    private Long c_req_link;

    private String c_req_review_sender;

    private String c_req_review_responder;

    private String c_req_review_creat_date;

    private String c_req_review_update_date;

    private String c_req_review_status;

    private String c_req_review_contents;

    private String c_req_review_etc;

}
