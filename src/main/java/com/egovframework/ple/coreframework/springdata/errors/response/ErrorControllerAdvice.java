package com.egovframework.ple.coreframework.springdata.errors.response;


import static com.egovframework.ple.coreframework.springdata.controller.CommonResponse.error;

import com.egovframework.ple.coreframework.springdata.controller.CommonResponse.ApiResult;
import com.egovframework.ple.coreframework.springdata.errors.exception.BaseException;
import com.egovframework.ple.coreframework.springdata.errors.exception.InvalidParamException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
* CommonControllerAdvice의 설명을 여기에 작성한다.
*
* @author MZC01-DJSHIN
* @version 1.0, 작업 내용
* 작성일 2022-08-25
**/
@Slf4j
@ControllerAdvice
public class ErrorControllerAdvice {
    private static final List<ErrorCode> SPECIFIC_ALERT_TARGET_ERROR_CODE_LIST = new ArrayList<>();

    private ResponseEntity<ApiResult<?>> newResponse(Throwable throwable, HttpStatus status) {
        return newResponse(throwable.getMessage(), status);
    }

    private ResponseEntity<ApiResult<?>> newResponse(String message, HttpStatus status) {
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(error(message, status), headers, status);
    }

    private ResponseEntity<ApiResult<?>> newResponse(ErrorCode errorCode, HttpStatus status) {
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(error(errorCode, status), headers, status);
    }
    private ResponseEntity<ApiResult<?>> newResponse(String message, ErrorCode errorCode, HttpStatus status) {
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(error(message, errorCode, status), headers, status);
    }
    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return headers;
    }

    /**
     * http status: 500 AND result: FAIL
     * 시스템 예외 상황. 집중 모니터링 대상
     *
     */

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> onException(Exception e) {
        return newResponse(ErrorCode.COMMON_SYSTEM_ERROR,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * http status: 200 AND result: FAIL
     * 시스템은 이슈 없고, 비즈니스 로직 처리에서 에러가 발생함
     *
     */
    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<?> onBaseException(BaseException e) {

        if (SPECIFIC_ALERT_TARGET_ERROR_CODE_LIST.contains(e.getErrorCode())) {
            log.error("[BaseException] ,cause = {}, errorMsg = {}", NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        } else {
            log.warn("[BaseException] ,cause = {}, errorMsg = {}", NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        }
        return newResponse(e.getMessage(), e.getErrorCode(),HttpStatus.BAD_REQUEST);
    }

    /**
     * 예상치 않은 Exception 중에서 모니터링 skip 이 가능한 Exception 을 처리할 때
     * ex) ClientAbortException
     *
     */

    @ExceptionHandler(value = {ClientAbortException.class})
    public ResponseEntity<?> skipException(Exception e) {
        log.warn("[skipException] , cause = {}, errorMsg = {}", NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        return newResponse(ErrorCode.COMMON_SYSTEM_ERROR,HttpStatus.OK);
    }

    /**
     * http status: 400 AND result: FAIL
     * request parameter 에러
     *
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("[BaseException] ,errorMsg = {}", NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        BindingResult bindingResult = e.getBindingResult();
        FieldError fe = bindingResult.getFieldError();
        if (fe != null) {
            String message = "Request Error" + " " + fe.getField() + "=" + fe.getRejectedValue() + " (" + fe.getDefaultMessage() + ")";
            return newResponse(message, ErrorCode.COMMON_INVALID_PARAMETER,HttpStatus.BAD_REQUEST);
        } else {
            return newResponse(ErrorCode.COMMON_INVALID_PARAMETER,HttpStatus.BAD_REQUEST);
        }
    }
    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<?> bindException(BindException e) {
        log.warn("[BindException] ,errorMsg = {}", NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        BindingResult bindingResult = e.getBindingResult();

        if(bindingResult.getErrorCount()>0){
            String message = bindingResult.getAllErrors()
                    .stream().map(a -> Optional.ofNullable(a.getDefaultMessage())
                            .orElse("알려지지 않은 에러")).findFirst().orElseThrow(InvalidParamException::new);

            return newResponse(message, ErrorCode.COMMON_INVALID_PARAMETER,HttpStatus.BAD_REQUEST);
        }
        return newResponse(ErrorCode.COMMON_INVALID_PARAMETER,HttpStatus.BAD_REQUEST);
    }
}
