package com.arms.api.analysis.time.controller;

import com.arms.api.analysis.time.model.등고선데이터;
import com.arms.api.analysis.time.model.일자별_요구사항_연결된이슈_생성개수_및_상태데이터;
import com.arms.api.analysis.time.service.TimeService;
import com.arms.api.util.external_communicate.dto.search.검색결과_목록_메인;
import com.arms.api.util.external_communicate.dto.지라이슈;
import com.arms.api.util.external_communicate.dto.지라이슈_일자별_제품_및_제품버전_검색요청;
import com.arms.api.util.external_communicate.dto.지라이슈_제품_및_제품버전_검색요청;
import com.arms.api.util.external_communicate.dto.히트맵데이터;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RestController
@AllArgsConstructor
@RequestMapping(value = "/arms/analysis/time")
public class 일정분석_컨트롤러 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private final TimeService timeService;

    static final long dummy_jira_server = 0L;

    @GetMapping(value = "/pdService/pdServiceVersions")
    @ResponseBody
    public ModelAndView 제품서비스_버전목록으로_조회(@RequestParam Long pdServiceLink,
                                        @RequestParam List<Long> pdServiceVersionLinks) throws Exception {

        로그.info(" [ 일정분석_컨트롤러 :: 제품서비스_버전목록으로_조회 ] ");
        List<지라이슈> result = timeService.제품서비스_버전목록으로_조회(dummy_jira_server, pdServiceLink, pdServiceVersionLinks);

        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("result", result);
        return modelAndView;
    }

    @GetMapping(value = "/heatmap")
    @ResponseBody
    public ModelAndView 히트맵_제품서비스_버전목록으로_조회(@RequestParam Long pdServiceLink,
                                            @RequestParam List<Long> pdServiceVersionLinks) throws Exception {

        로그.info(" [ 일정분석_컨트롤러 :: 히트맵_제품서비스_버전목록으로_조회 ] ");
        히트맵데이터 result = timeService.히트맵_제품서비스_버전목록으로_조회(dummy_jira_server, pdServiceLink, pdServiceVersionLinks);

        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("result", result);
        return modelAndView;
    }

    @ResponseBody
    @GetMapping("/normal-version/resolution")
    public ModelAndView 제품서비스_일반_버전_해결책유무_집계(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청,
                                             @RequestParam(required = false) String resolution) {

        로그.info(" [ 일정분석_컨트롤러 :: 제품서비스_일반_버전_해결책유무_집계 ] " +
                        "pdServiceId ==> {}, pdServiceVersionLinks ==> {}, resolution ==> {}"
                , 지라이슈_제품_및_제품버전_검색요청.getPdServiceLink()
                , 지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks().toString()
                , resolution);

        검색결과_목록_메인 요구사항_연결이슈_일반_버전_해결책통계
                = timeService.제품서비스_일반_버전_해결책유무_통계(지라이슈_제품_및_제품버전_검색요청, resolution);

        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("result", 요구사항_연결이슈_일반_버전_해결책통계);
        return modelAndView;
    }

    @ResponseBody
    @GetMapping("/standard-daily/jira-issue")
    public ModelAndView 기준일자별_제품_및_제품버전목록_요구사항_및_연결된이슈_집계(지라이슈_일자별_제품_및_제품버전_검색요청 지라이슈_일자별_제품_및_제품버전_검색요청) throws Exception {

        로그.info("[일정분석_컨트롤러 :: 기준일자별_제품_및_제품버전목록_요구사항_및_연결된이슈_집계] " +
                ":: 지라이슈 일자별 제품 및 제품버전 검색요청 -> " + 지라이슈_일자별_제품_및_제품버전_검색요청.toString());

        Map<String, 일자별_요구사항_연결된이슈_생성개수_및_상태데이터> result =
                timeService.기준일자별_제품_및_제품버전목록_요구사항_및_연결된이슈_집계(지라이슈_일자별_제품_및_제품버전_검색요청);

        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("result", result);
        return modelAndView;
    }

    @ResponseBody
    @GetMapping("/standard-daily/updated-jira-issue")
    public ModelAndView 기준일자별_제품_및_제품버전목록_업데이트된_이슈조회(지라이슈_일자별_제품_및_제품버전_검색요청 지라이슈_일자별_제품_및_제품버전_검색요청) throws Exception {

        로그.info("[일정분석_컨트롤러 :: 기준일자별_제품_및_제품버전목록_업데이트된_이슈조회] " +
                ":: 지라이슈 일자별 제품 및 제품버전 검색요청 -> " + 지라이슈_일자별_제품_및_제품버전_검색요청.toString());


        Map<Long, List<지라이슈>> 버전별_그룹화_결과
                = timeService.기준일자별_제품_및_제품버전목록_업데이트된_이슈조회(지라이슈_일자별_제품_및_제품버전_검색요청);

        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("result", 버전별_그룹화_결과);

        return modelAndView;

    }

    @ResponseBody
    @GetMapping("/standard-daily/updated-ridgeline")
    public List<등고선데이터> 기준일자별_제품_및_제품버전목록_업데이트된_누적_이슈조회(지라이슈_일자별_제품_및_제품버전_검색요청 지라이슈_일자별_제품_및_제품버전_검색요청) throws Exception {

        log.info("[일정분석_컨트롤러 :: 기준일자별_제품_및_제품버전목록_업데이트된_누적_이슈조회] " +
                ":: 지라이슈 일자별 제품 및 제품버전 검색요청 -> " + 지라이슈_일자별_제품_및_제품버전_검색요청.toString());

        List<등고선데이터>  결과 = timeService.기준일자별_제품_및_제품버전목록_업데이트된_누적_이슈조회(지라이슈_일자별_제품_및_제품버전_검색요청);

        return 결과;
    }

}
