ALTER TABLE `aRMS`.`T_ARMS_REQSTATE`
    ADD COLUMN c_check         text null comment '암스 기본 상태 설정 필드';

Update `aRMS`.`T_ARMS_REQSTATE` set C_STATE_CATEGORY_MAPPING_ID = 3 Where C_ID = 10;
Update `aRMS`.`T_ARMS_REQSTATE` set C_STATE_CATEGORY_MAPPING_ID = 4 Where C_ID = 11;
Update `aRMS`.`T_ARMS_REQSTATE` set C_STATE_CATEGORY_MAPPING_ID = 5 Where C_ID = 12;
Update `aRMS`.`T_ARMS_REQSTATE` set C_STATE_CATEGORY_MAPPING_ID = 6 Where C_ID = 13;

Delete From `aRMS`.`T_ARMS_REQSTATE` Where C_ID IN (3, 4, 5, 6, 7, 8, 9);