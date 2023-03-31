--
-- Table structure for table `GLOBAL_TREE_MAP`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`GLOBAL_TREE_MAP` (

    `filerepository_link`                   bigint(20) default NULL COMMENT '파일',

    `jiraconnectinfo_link`                   bigint(20) default NULL COMMENT '파일',
    `jiraissue_link`                            bigint(20) default NULL COMMENT '파일',
    `jiraissuepriority_link`                   bigint(20) default NULL COMMENT '파일',

    `pdservice_link`                        bigint(20) default NULL COMMENT '제품(서비스)',
    `pdserviceversion_link`                 bigint(20) default NULL COMMENT '제품(서비스) 버전'

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='트리 맵';



--
-- Table structure for table `T_ARMS_FILEREPOSITORY`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_FILEREPOSITORY_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_file_name`                    text NULL,
    `c_file_content_type`            text NULL,
    `c_file_server_sub_path`         text NULL,
    `c_file_physical_name`           text NULL,
    `c_file_size`                    bigint(20) NULL,
    `c_file_tag_name`                text NULL,
    `c_file_url`                     text NULL,
    `c_file_thumbnail_url`           text NULL,
    `c_file_delete_url`              text NULL,
    `c_file_delete_type`             text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='파일 레파지토리 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_FILEREPOSITORY` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_file_name`                    text NULL,
    `c_file_content_type`            text NULL,
    `c_file_server_sub_path`         text NULL,
    `c_file_physical_name`           text NULL,
    `c_file_size`                    bigint(20) NULL,
    `c_file_tag_name`                text NULL,
    `c_file_url`                     text NULL,
    `c_file_thumbnail_url`           text NULL,
    `c_file_delete_url`              text NULL,
    `c_file_delete_type`             text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='파일 레파지토리';


Insert into `aRMS`.`T_ARMS_FILEREPOSITORY` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_FILEREPOSITORY', 'root');
Insert into `aRMS`.`T_ARMS_FILEREPOSITORY` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '파일 레파지토리', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_FILEREPOSITORY
    BEFORE  INSERT ON T_ARMS_FILEREPOSITORY
    FOR EACH ROW
BEGIN
    insert into T_ARMS_FILEREPOSITORY_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_FILEREPOSITORY
    BEFORE  UPDATE ON T_ARMS_FILEREPOSITORY
    FOR EACH ROW
BEGIN
    insert into T_ARMS_FILEREPOSITORY_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_FILEREPOSITORY_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_FILEREPOSITORY
    BEFORE  DELETE ON T_ARMS_FILEREPOSITORY
    FOR EACH ROW
BEGIN
    insert into T_ARMS_FILEREPOSITORY_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;


--
-- Table structure for table `T_ARMS_PDSERVICEVERSION`
--

CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_PDSERVICEVERSION_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_pdservice_link`          bigint(20) NULL,

    `c_pds_version_contents`    longtext NULL,
    `c_pds_version_etc`         text NULL,

    `c_pds_version_start_date`  text NULL,
    `c_pds_version_end_date`    text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='제품(서비스) 버전 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_PDSERVICEVERSION` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_pdservice_link`          bigint(20) NULL,

    `c_pds_version_contents`    longtext NULL,
    `c_pds_version_etc`         text NULL,

    `c_pds_version_start_date`  text NULL,
    `c_pds_version_end_date`    text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='제품(서비스) 버전';


Insert into `aRMS`.`T_ARMS_PDSERVICEVERSION` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_PDSERVICEVERSION', 'root');
Insert into `aRMS`.`T_ARMS_PDSERVICEVERSION` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '제품(서비스) 버전', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_PDSERVICEVERSION
    BEFORE  INSERT ON T_ARMS_PDSERVICEVERSION
    FOR EACH ROW
BEGIN
    insert into T_ARMS_PDSERVICEVERSION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_PDSERVICEVERSION
    BEFORE  UPDATE ON T_ARMS_PDSERVICEVERSION
    FOR EACH ROW
BEGIN
    insert into T_ARMS_PDSERVICEVERSION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_PDSERVICEVERSION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_PDSERVICEVERSION
    BEFORE  DELETE ON T_ARMS_PDSERVICEVERSION
    FOR EACH ROW
BEGIN
    insert into T_ARMS_PDSERVICEVERSION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;


--
-- Table structure for table `T_ARMS_PDSERVICE`
--

CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_PDSERVICE_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_pdservice_contents`      longtext NULL,
    `c_pdservice_etc`           text NULL,

    `c_pdservice_owner`         text NULL,
    `c_pdservice_reviewer01`    text NULL,
    `c_pdservice_reviewer02`    text NULL,
    `c_pdservice_reviewer03`    text NULL,
    `c_pdservice_reviewer04`    text NULL,
    `c_pdservice_reviewer05`    text NULL,
    `c_pdservice_writer`        text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='제품(서비스) 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_PDSERVICE` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_pdservice_contents`      longtext NULL,
    `c_pdservice_etc`           text NULL,

    `c_pdservice_owner`         text NULL,
    `c_pdservice_reviewer01`    text NULL,
    `c_pdservice_reviewer02`    text NULL,
    `c_pdservice_reviewer03`    text NULL,
    `c_pdservice_reviewer04`    text NULL,
    `c_pdservice_reviewer05`    text NULL,
    `c_pdservice_writer`        text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='제품(서비스)';


Insert into `aRMS`.`T_ARMS_PDSERVICE` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_PDSERVICE', 'root');
Insert into `aRMS`.`T_ARMS_PDSERVICE` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '제품(서비스)', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_PDSERVICE
    BEFORE  INSERT ON T_ARMS_PDSERVICE
    FOR EACH ROW
BEGIN
    insert into T_ARMS_PDSERVICE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_PDSERVICE
    BEFORE  UPDATE ON T_ARMS_PDSERVICE
    FOR EACH ROW
BEGIN
    insert into T_ARMS_PDSERVICE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_PDSERVICE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_PDSERVICE
    BEFORE  DELETE ON T_ARMS_PDSERVICE
    FOR EACH ROW
BEGIN
    insert into T_ARMS_PDSERVICE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;


--
-- Table structure for table `T_ARMS_JIRAPROJECT`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAPROJECT_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_jira_contents`           longtext NULL,
    `c_jira_etc`                text NULL,

    `c_jira_url`                text NULL,
    `c_jira_id`                 text NULL,
    `c_jira_key`                text NULL,
    `c_jira_name`               text NULL,

    `c_jira_avatar_48`          text NULL,
    `c_jira_avatar_32`          text NULL,
    `c_jira_avatar_24`          text NULL,
    `c_jira_avatar_16`          text NULL,

    `c_jira_category_url`       text NULL,
    `c_jira_category_id`        text NULL,
    `c_jira_category_name`      text NULL,
    `c_jira_category_desc`      text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 프로젝트 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAPROJECT` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_jira_contents`           longtext NULL,
    `c_jira_etc`                text NULL,

    `c_jira_url`                text NULL,
    `c_jira_id`                 text NULL,
    `c_jira_key`                text NULL,
    `c_jira_name`               text NULL,

    `c_jira_avatar_48`          text NULL,
    `c_jira_avatar_32`          text NULL,
    `c_jira_avatar_24`          text NULL,
    `c_jira_avatar_16`          text NULL,

    `c_jira_category_url`       text NULL,
    `c_jira_category_id`        text NULL,
    `c_jira_category_name`      text NULL,
    `c_jira_category_desc`      text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 프로젝트';


Insert into `aRMS`.`T_ARMS_JIRAPROJECT` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_JIRAPROJECT', 'root');
Insert into `aRMS`.`T_ARMS_JIRAPROJECT` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '지라 프로젝트', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_JIRAPROJECT
    BEFORE  INSERT ON T_ARMS_JIRAPROJECT
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAPROJECT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_JIRAPROJECT
    BEFORE  UPDATE ON T_ARMS_JIRAPROJECT
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAPROJECT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_JIRAPROJECT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_JIRAPROJECT
    BEFORE  DELETE ON T_ARMS_JIRAPROJECT
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAPROJECT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;



--
-- Table structure for table `T_ARMS_JIRAPROJECTVERSION`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAPROJECTVERSION_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_jira_link`                   bigint(20) NULL,

    `c_jira_version_url`            text NULL,
    `c_jira_version_id`             text NULL,
    `c_jira_version_desc`           text NULL,
    `c_jira_version_name`           text NULL,
    `c_jira_version_projectid`      text NULL,
    `c_jira_version_archived`       text NULL,
    `c_jira_version_released`       text NULL,
    `c_jira_version_releaseDate`    text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 프로젝트 버전 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAPROJECTVERSION` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_jira_link`                   bigint(20) NULL,

    `c_jira_version_url`            text NULL,
    `c_jira_version_id`             text NULL,
    `c_jira_version_desc`           text NULL,
    `c_jira_version_name`           text NULL,
    `c_jira_version_projectid`      text NULL,
    `c_jira_version_archived`       text NULL,
    `c_jira_version_released`       text NULL,
    `c_jira_version_releaseDate`    text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 프로젝트 버전';


Insert into `aRMS`.`T_ARMS_JIRAPROJECTVERSION` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_JIRAPROJECTVERSION', 'root');
Insert into `aRMS`.`T_ARMS_JIRAPROJECTVERSION` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '지라 프로젝트 버전', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_JIRAPROJECTVERSION
    BEFORE  INSERT ON T_ARMS_JIRAPROJECTVERSION
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAPROJECTVERSION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_JIRAPROJECTVERSION
    BEFORE  UPDATE ON T_ARMS_JIRAPROJECTVERSION
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAPROJECTVERSION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_JIRAPROJECTVERSION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_JIRAPROJECTVERSION
    BEFORE  DELETE ON T_ARMS_JIRAPROJECTVERSION
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAPROJECTVERSION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;


--
-- Table structure for table `T_ARMS_JIRASERVER`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRASERVER_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_jira_server_contents`    longtext NULL,
    `c_jira_server_etc`         text NULL,

    `c_jira_server_base_url`    text NULL,
    `c_jira_server_version`     text NULL,
    `c_jira_server_build`       text NULL,
    `c_jira_server_title`       text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 서버 정보 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRASERVER` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_jira_server_contents`    longtext NULL,
    `c_jira_server_etc`         text NULL,

    `c_jira_server_base_url`    text NULL,
    `c_jira_server_version`     text NULL,
    `c_jira_server_build`       text NULL,
    `c_jira_server_title`       text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 서버 정보';


Insert into `aRMS`.`T_ARMS_JIRASERVER` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_JIRASERVER', 'root');
Insert into `aRMS`.`T_ARMS_JIRASERVER` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '지라 서버 정보', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_JIRASERVER
    BEFORE  INSERT ON T_ARMS_JIRASERVER
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRASERVER_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_JIRASERVER
    BEFORE  UPDATE ON T_ARMS_JIRASERVER
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRASERVER_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_JIRASERVER_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_JIRASERVER
    BEFORE  DELETE ON T_ARMS_JIRASERVER
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRASERVER_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;


--
-- Table structure for table `T_ARMS_JIRAISSUE`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAISSUE_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_issue_id`                text NULL,
    `c_issue_url`               text NULL,
    `c_issue_desc`              text NULL,
    `c_issue_key`               text NULL,
    `c_issue_summary`           text NULL,

    `c_issue_type`              text NULL,
    `c_issue_labels`            text NULL,
    `c_issue_components`        text NULL,

    `c_issue_link_yn`           text NULL,
    `c_issue_subtask_yn`        text NULL,

    `c_issue_affected_versions` text NULL,
    `c_issue_fix_versions`      text NULL,

    `c_issue_create_date`       text NULL,
    `c_issue_update_date`       text NULL,
    `c_issue_due_date`          text NULL,

    `c_issue_status`            bigint(20) NULL,
    `c_issue_priority`          bigint(20) NULL,
    `c_issue_resolution`        bigint(20) NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 이슈 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAISSUE` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_issue_id`                text NULL,
    `c_issue_url`               text NULL,
    `c_issue_desc`              text NULL,
    `c_issue_key`               text NULL,
    `c_issue_summary`           text NULL,

    `c_issue_type`              text NULL,
    `c_issue_labels`            text NULL,
    `c_issue_components`        text NULL,

    `c_issue_link_yn`           text NULL,
    `c_issue_subtask_yn`        text NULL,

    `c_issue_affected_versions` text NULL,
    `c_issue_fix_versions`      text NULL,

    `c_issue_create_date`       text NULL,
    `c_issue_update_date`       text NULL,
    `c_issue_due_date`          text NULL,

    `c_issue_status`            bigint(20) NULL,
    `c_issue_priority`          bigint(20) NULL,
    `c_issue_resolution`        bigint(20) NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 이슈';


Insert into `aRMS`.`T_ARMS_JIRAISSUE` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_JIRAISSUE', 'root');
Insert into `aRMS`.`T_ARMS_JIRAISSUE` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '지라 이슈', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_JIRAISSUE
    BEFORE  INSERT ON T_ARMS_JIRAISSUE
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_JIRAISSUE
    BEFORE  UPDATE ON T_ARMS_JIRAISSUE
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_JIRAISSUE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_JIRAISSUE
    BEFORE  DELETE ON T_ARMS_JIRAISSUE
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;




--
-- Table structure for table `T_ARMS_JIRAISSUETYPE`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAISSUETYPE_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_issue_type_id`           text NULL,
    `c_issue_type_desc`         text NULL,
    `c_issue_type_name`         text NULL,
    `c_issue_type_url`          text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 이슈 타입 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAISSUETYPE` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_issue_type_id`           text NULL,
    `c_issue_type_desc`         text NULL,
    `c_issue_type_name`         text NULL,
    `c_issue_type_url`          text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 이슈 타입';


Insert into `aRMS`.`T_ARMS_JIRAISSUETYPE` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_JIRAISSUETYPE', 'root');
Insert into `aRMS`.`T_ARMS_JIRAISSUETYPE` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '지라 이슈 타입', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_JIRAISSUETYPE
    BEFORE  INSERT ON T_ARMS_JIRAISSUETYPE
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUETYPE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_JIRAISSUETYPE
    BEFORE  UPDATE ON T_ARMS_JIRAISSUETYPE
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUETYPE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_JIRAISSUETYPE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_JIRAISSUETYPE
    BEFORE  DELETE ON T_ARMS_JIRAISSUETYPE
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUETYPE_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;



--
-- Table structure for table `T_ARMS_JIRAISSUEPRIORITY`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAISSUEPRIORITY_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_issue_priority_id`       text NULL,
    `c_issue_priority_desc`     text NULL,
    `c_issue_priority_name`     text NULL,
    `c_issue_priority_url`      text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 이슈 우선순위 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAISSUEPRIORITY` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_issue_priority_id`       text NULL,
    `c_issue_priority_desc`     text NULL,
    `c_issue_priority_name`     text NULL,
    `c_issue_priority_url`      text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 이슈 우선순위';


Insert into `aRMS`.`T_ARMS_JIRAISSUEPRIORITY` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_JIRAISSUEPRIORITY', 'root');
Insert into `aRMS`.`T_ARMS_JIRAISSUEPRIORITY` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '지라 이슈 우선순위', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_JIRAISSUEPRIORITY
    BEFORE  INSERT ON T_ARMS_JIRAISSUEPRIORITY
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUEPRIORITY_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_JIRAISSUEPRIORITY
    BEFORE  UPDATE ON T_ARMS_JIRAISSUEPRIORITY
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUEPRIORITY_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_JIRAISSUEPRIORITY_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_JIRAISSUEPRIORITY
    BEFORE  DELETE ON T_ARMS_JIRAISSUEPRIORITY
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUEPRIORITY_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;



--
-- Table structure for table `T_ARMS_JIRAISSUESTATUS`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAISSUESTATUS_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_issue_status_id`       text NULL,
    `c_issue_status_desc`     text NULL,
    `c_issue_status_name`     text NULL,
    `c_issue_status_url`      text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 이슈 상태값 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAISSUESTATUS` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_issue_status_id`       text NULL,
    `c_issue_status_desc`     text NULL,
    `c_issue_status_name`     text NULL,
    `c_issue_status_url`      text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 이슈 상태값';


Insert into `aRMS`.`T_ARMS_JIRAISSUESTATUS` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_JIRAISSUESTATUS', 'root');
Insert into `aRMS`.`T_ARMS_JIRAISSUESTATUS` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '지라 이슈 상태값', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_JIRAISSUESTATUS
    BEFORE  INSERT ON T_ARMS_JIRAISSUESTATUS
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUESTATUS_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_JIRAISSUESTATUS
    BEFORE  UPDATE ON T_ARMS_JIRAISSUESTATUS
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUESTATUS_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_JIRAISSUESTATUS_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_JIRAISSUESTATUS
    BEFORE  DELETE ON T_ARMS_JIRAISSUESTATUS
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUESTATUS_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;


--
-- Table structure for table `T_ARMS_JIRAISSUERESOLUTION`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAISSUERESOLUTION_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_issue_resolution_id`     text NULL,
    `c_issue_resolution_desc`   text NULL,
    `c_issue_resolution_name`   text NULL,
    `c_issue_resolution_url`    text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 이슈 해결책 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_JIRAISSUERESOLUTION` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_issue_resolution_id`     text NULL,
    `c_issue_resolution_desc`   text NULL,
    `c_issue_resolution_name`   text NULL,
    `c_issue_resolution_url`    text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='지라 이슈 해결책';


Insert into `aRMS`.`T_ARMS_JIRAISSUERESOLUTION` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_JIRAISSUERESOLUTION', 'root');
Insert into `aRMS`.`T_ARMS_JIRAISSUERESOLUTION` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '지라 이슈 해결책', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_JIRAISSUERESOLUTION
    BEFORE  INSERT ON T_ARMS_JIRAISSUERESOLUTION
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUERESOLUTION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_JIRAISSUERESOLUTION
    BEFORE  UPDATE ON T_ARMS_JIRAISSUERESOLUTION
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUERESOLUTION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_JIRAISSUERESOLUTION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_JIRAISSUERESOLUTION
    BEFORE  DELETE ON T_ARMS_JIRAISSUERESOLUTION
    FOR EACH ROW
BEGIN
    insert into T_ARMS_JIRAISSUERESOLUTION_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;



--
-- Table structure for table `T_ARMS_REQADD`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_REQADD_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_pdservice_link`          bigint(20) NULL,
    `c_version_link`            bigint(20) NULL,

    `c_req_reviewer01`          text NULL,
    `c_req_reviewer01_status`   text NULL,
    `c_req_reviewer02`          text NULL,
    `c_req_reviewer02_status`   text NULL,
    `c_req_reviewer03`          text NULL,
    `c_req_reviewer03_status`   text NULL,
    `c_req_reviewer04`          text NULL,
    `c_req_reviewer04_status`   text NULL,
    `c_req_reviewer05`          text NULL,
    `c_req_reviewer05_status`   text NULL,
    `c_req_writer`              text NULL,
    `c_req_create_date`         text NULL,
    `c_req_priority_link`       bigint(20) NULL,
    `c_req_contents`            longtext NULL,
    `c_req_etc`                 text NULL,
    `c_req_status_link`         bigint(20) NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='요구사항 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_REQADD` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_pdservice_link`          bigint(20) NULL,
    `c_version_link`            bigint(20) NULL,

    `c_req_reviewer01`          text NULL,
    `c_req_reviewer01_status`   text NULL,
    `c_req_reviewer02`          text NULL,
    `c_req_reviewer02_status`   text NULL,
    `c_req_reviewer03`          text NULL,
    `c_req_reviewer03_status`   text NULL,
    `c_req_reviewer04`          text NULL,
    `c_req_reviewer04_status`   text NULL,
    `c_req_reviewer05`          text NULL,
    `c_req_reviewer05_status`   text NULL,
    `c_req_writer`              text NULL,
    `c_req_create_date`         text NULL,
    `c_req_priority_link`       bigint(20) NULL,
    `c_req_contents`            longtext NULL,
    `c_req_etc`                 text NULL,
    `c_req_status_link`         bigint(20) NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='요구사항';


Insert into `aRMS`.`T_ARMS_REQADD` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_REQADD', 'root');
Insert into `aRMS`.`T_ARMS_REQADD` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '요구사항', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_REQADD
    BEFORE  INSERT ON T_ARMS_REQADD
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQADD_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_REQADD
    BEFORE  UPDATE ON T_ARMS_REQADD
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQADD_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_REQADD_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_REQADD
    BEFORE  DELETE ON T_ARMS_REQADD
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQADD_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;



--
-- Table structure for table `T_ARMS_REQCOMMENT`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_REQCOMMENT_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_pdservice_link`          bigint(20) NULL,
    `c_version_link`            bigint(20) NULL,
    `c_req_link`                bigint(20) NULL,

    `c_req_comment_sender`      text NULL,
    `c_req_comment_date`        text NULL,
    `c_req_comment_contents`    text NULL,
    `c_req_comment_etc`         text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='요구사항 커멘트 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_REQCOMMENT` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_pdservice_link`          bigint(20) NULL,
    `c_version_link`            bigint(20) NULL,
    `c_req_link`                bigint(20) NULL,

    `c_req_comment_sender`      text NULL,
    `c_req_comment_date`        text NULL,
    `c_req_comment_contents`    text NULL,
    `c_req_comment_etc`         text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='요구사항 커멘트';


Insert into `aRMS`.`T_ARMS_REQCOMMENT` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_REQCOMMENT', 'root');
Insert into `aRMS`.`T_ARMS_REQCOMMENT` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '요구사항 커멘트', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_REQCOMMENT
    BEFORE  INSERT ON T_ARMS_REQCOMMENT
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQCOMMENT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_REQCOMMENT
    BEFORE  UPDATE ON T_ARMS_REQCOMMENT
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQCOMMENT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_REQCOMMENT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_REQCOMMENT
    BEFORE  DELETE ON T_ARMS_REQCOMMENT
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQCOMMENT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;


--
-- Table structure for table `T_ARMS_REQREVIEW`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_REQREVIEW_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_pdservice_link`          bigint(20) NULL,
    `c_version_link`            bigint(20) NULL,
    `c_req_link`                bigint(20) NULL,

    `c_req_review_sender`       text NULL,
    `c_req_review_responder`    text NULL,
    `c_req_review_creat_date`   text NULL,
    `c_req_review_update_date`  text NULL,
    `c_req_review_status`       text NULL,
    `c_req_review_contents`     longtext NULL,
    `c_req_review_etc`          text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='요구사항 리뷰 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_REQREVIEW` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_pdservice_link`          bigint(20) NULL,
    `c_version_link`            bigint(20) NULL,
    `c_req_link`                bigint(20) NULL,

    `c_req_review_sender`       text NULL,
    `c_req_review_responder`    text NULL,
    `c_req_review_creat_date`   text NULL,
    `c_req_review_update_date`  text NULL,
    `c_req_review_status`       text NULL,
    `c_req_review_contents`     longtext NULL,
    `c_req_review_etc`          text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='요구사항 리뷰';


Insert into `aRMS`.`T_ARMS_REQREVIEW` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_REQREVIEW', 'root');
Insert into `aRMS`.`T_ARMS_REQREVIEW` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '요구사항 리뷰', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_REQREVIEW
    BEFORE  INSERT ON T_ARMS_REQREVIEW
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQREVIEW_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_REQREVIEW
    BEFORE  UPDATE ON T_ARMS_REQREVIEW
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQREVIEW_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_REQREVIEW_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_REQREVIEW
    BEFORE  DELETE ON T_ARMS_REQREVIEW
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQREVIEW_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;

--
-- Table structure for table `T_ARMS_REQREVIEWCOMMENT`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_REQREVIEWCOMMENT_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    `c_pdservice_link`          bigint(20) NULL,
    `c_version_link`            bigint(20) NULL,
    `c_req_link`                bigint(20) NULL,
    `c_req_review_link`         bigint(20) NULL,

    `c_req_review_comment_sender`      text NULL,
    `c_req_review_comment_date`        text NULL,
    `c_req_review_comment_contents`    text NULL,
    `c_req_review_comment_etc`         text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='요구사항 리뷰 커멘트 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_REQREVIEWCOMMENT` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_pdservice_link`          bigint(20) NULL,
    `c_version_link`            bigint(20) NULL,
    `c_req_link`                bigint(20) NULL,
    `c_req_review_link`         bigint(20) NULL,

    `c_req_review_comment_sender`      text NULL,
    `c_req_review_comment_date`        text NULL,
    `c_req_review_comment_contents`    text NULL,
    `c_req_review_comment_etc`         text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='요구사항 리뷰 커멘트 트리거 로그';

Insert into `aRMS`.`T_ARMS_REQREVIEWCOMMENT` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_REQREVIEWCOMMENT', 'root');
Insert into `aRMS`.`T_ARMS_REQREVIEWCOMMENT` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '요구사항 리뷰 커멘트', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_REQREVIEWCOMMENT
    BEFORE  INSERT ON T_ARMS_REQREVIEWCOMMENT
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQREVIEWCOMMENT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_REQREVIEWCOMMENT
    BEFORE  UPDATE ON T_ARMS_REQREVIEWCOMMENT
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQREVIEWCOMMENT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_REQREVIEWCOMMENT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_REQREVIEWCOMMENT
    BEFORE  DELETE ON T_ARMS_REQREVIEWCOMMENT
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQREVIEWCOMMENT_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;


--
-- Table structure for table `T_ARMS_REQSTATUS`
--
CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_REQSTATUS_LOG` (

    `c_id`                      bigint(20) NOT NULL COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    `c_method`                  text NULL COMMENT '노드 변경 행위',
    `c_state`                   text NULL COMMENT '노드 상태값 ( 이전인지. 이후인지)',
    `c_date`                    date NULL COMMENT '노드 변경 시',

    -- 제품 서비스
    `c_pdservice_link`              bigint(20) NULL,
    `c_pdservice_name`              text NULL,

    -- 제품 서비스 버전
    `c_pds_version_link`            bigint(20) NULL,
    `c_pds_version_name`            text NULL,

    -- 제품 서비스 연결 지라 프로젝트
    `c_jira_link`                   bigint(20) NULL,
    `c_jira_key`                    text NULL,
    `c_jira_url`                    text NULL,

    -- 제품 서비스 연결 지라 프로젝트 버전
    `c_jira_verison_link`           bigint(20) NULL,
    `c_jira_verison_name`           text NULL,
    `c_jira_verison_url`            text NULL,

    -- 요구사항
    `c_req_link`                    bigint(20) NULL,
    `c_req_name`                    text NULL,

    -- 요구사항 우선 순위
    `c_req_priority_link`           bigint(20) NULL,
    `c_req_priority_name`           text NULL,

    -- 요구사항 상태
    `c_req_status_link`             bigint(20) NULL,
    `c_req_status_name`             text NULL,

    -- 요구사항 이슈
    `c_issue_link`                  bigint(20) NULL,
    `c_issue_key`                   text NULL,
    `c_issue_summery`               text NULL,
    `c_issue_url`                   text NULL,

    -- 요구사항 이슈 우선순위
    `c_issue_priority_link`         bigint(20) NULL,
    `c_issue_priority_name`         text NULL,
    `c_issue_priority_url`          text NULL,

    -- 요구사항 이슈 상태
    `c_issue_status_link`           bigint(20) NULL,
    `c_issue_status_name`           text NULL,
    `c_issue_status_url`            text NULL,

    -- 요구사항 이슈 해결책
    `c_issue_resolution_link`       bigint(20) NULL,
    `c_issue_resolution_name`       text NULL,
    `c_issue_resolution_url`        text NULL,

    -- 요구사항 리뷰 상황
    `c_req_review_status`           text NULL,

    -- 요구사항 생성자
    `c_writer`                      text NULL,
    `c_create_date`                 date NUlL,

    -- 요구사항 이슈 연관, 서브 이슈 서머리
    `c_issue_link_issue_summary`    text NULL,
    `c_issue_sub_issue_summary`     text NULL,

    -- 기타
    `c_req_status_etc`              text NULL,
    `c_req_status_contents`         text NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='요구사항 - 이슈 결과 장표 트리거 로그';


CREATE TABLE IF NOT EXISTS `aRMS`.`T_ARMS_REQSTATUS` (

    `c_id`                      bigint(20) AUTO_INCREMENT primary key COMMENT '노드 아이디',
    `c_parentid`                bigint(20) NOT NULL COMMENT '부모 노드 아이디',
    `c_position`                bigint(20) NOT NULL COMMENT '노드 포지션',
    `c_left`                    bigint(20) NOT NULL COMMENT '노드 좌측 끝 포인트',
    `c_right`                   bigint(20) NOT NULL COMMENT '노드 우측 끝 포인트',
    `c_level`                   bigint(20) NOT NULL COMMENT '노드 DEPTH',
    `c_title`                   VARCHAR(255) COMMENT '노드 명',
    `c_type`                    VARCHAR(255) COMMENT '노드 타입',

    -- 제품 서비스
    `c_pdservice_link`              bigint(20) NULL,
    `c_pdservice_name`              text NULL,

    -- 제품 서비스 버전
    `c_pds_version_link`            bigint(20) NULL,
    `c_pds_version_name`            text NULL,

    -- 제품 서비스 연결 지라 프로젝트
    `c_jira_link`                   bigint(20) NULL,
    `c_jira_key`                    text NULL,
    `c_jira_url`                    text NULL,

    -- 제품 서비스 연결 지라 프로젝트 버전
    `c_jira_verison_link`           bigint(20) NULL,
    `c_jira_verison_name`           text NULL,
    `c_jira_verison_url`            text NULL,

    -- 요구사항
    `c_req_link`                    bigint(20) NULL,
    `c_req_name`                    text NULL,

    -- 요구사항 우선 순위
    `c_req_priority_link`           bigint(20) NULL,
    `c_req_priority_name`           text NULL,

    -- 요구사항 상태
    `c_req_status_link`             bigint(20) NULL,
    `c_req_status_name`             text NULL,

    -- 요구사항 이슈
    `c_issue_link`                  bigint(20) NULL,
    `c_issue_key`                   text NULL,
    `c_issue_summery`               text NULL,
    `c_issue_url`                   text NULL,

    -- 요구사항 이슈 우선순위
    `c_issue_priority_link`         bigint(20) NULL,
    `c_issue_priority_name`         text NULL,
    `c_issue_priority_url`          text NULL,

    -- 요구사항 이슈 상태
    `c_issue_status_link`           bigint(20) NULL,
    `c_issue_status_name`           text NULL,
    `c_issue_status_url`            text NULL,

    -- 요구사항 이슈 해결책
    `c_issue_resolution_link`       bigint(20) NULL,
    `c_issue_resolution_name`       text NULL,
    `c_issue_resolution_url`        text NULL,

    -- 요구사항 리뷰 상황
    `c_req_review_status`           text NULL,

    -- 요구사항 생성자
    `c_writer`                      text NULL,
    `c_create_date`                 date NUlL,

    -- 요구사항 이슈 연관, 서브 이슈 서머리
    `c_issue_link_issue_summary`    text NULL,
    `c_issue_sub_issue_summary`     text NULL,

    -- 기타
    `c_req_status_etc`              text NULL,
    `c_req_status_contents`         text NULL

) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='요구사항 - 이슈 결과 장표';


Insert into `aRMS`.`T_ARMS_REQSTATUS` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (1, 0, 0, 1, 4, 0, 'T_ARMS_REQSTATUS', 'root');
Insert into `aRMS`.`T_ARMS_REQSTATUS` (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE)
Values (2, 1, 0, 2, 3, 1, '요구사항 - 이슈 결과 장표', 'drive');


DELIMITER $$
CREATE TRIGGER TG_INSERT_T_ARMS_REQSTATUS
    BEFORE  INSERT ON T_ARMS_REQSTATUS
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQSTATUS_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이전데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_UPDATE_T_ARMS_REQSTATUS
    BEFORE  UPDATE ON T_ARMS_REQSTATUS
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQSTATUS_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'update','변경이전데이터',now());
    insert into T_ARMS_REQSTATUS_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (NEW.C_ID,NEW.C_PARENTID,NEW.C_POSITION,NEW.C_LEFT,NEW.C_RIGHT,NEW.C_LEVEL,NEW.C_TITLE,NEW.C_TYPE,'update','변경이후데이터',now());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER TG_DELETE_T_ARMS_REQSTATUS
    BEFORE  DELETE ON T_ARMS_REQSTATUS
    FOR EACH ROW
BEGIN
    insert into T_ARMS_REQSTATUS_LOG (C_ID, C_PARENTID, C_POSITION, C_LEFT, C_RIGHT, C_LEVEL, C_TITLE, C_TYPE, C_METHOD, C_STATE, C_DATE)
    values (OLD.C_ID,OLD.C_PARENTID,OLD.C_POSITION,OLD.C_LEFT,OLD.C_RIGHT,OLD.C_LEVEL,OLD.C_TITLE,OLD.C_TYPE,'delete','삭제된데이터',now());
END $$
DELIMITER ;