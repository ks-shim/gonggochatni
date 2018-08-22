package dwayne.shim.gonggochatni.front.service.controller;

import dwayne.shim.gonggochatni.front.service.constants.ModelField;
import dwayne.shim.gonggochatni.front.service.model.Job2DepthInfo;
import dwayne.shim.gonggochatni.front.service.service.FrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class FrontController {

    @Resource
    private FrontService frontService;

    @RequestMapping(value = {"", "/", "/main"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET})
    public String showMainPage(Model model,
                               HttpSession session) {

        List<Job2DepthInfo> result = frontService.getNewJobs();
        model.addAttribute(ModelField.JOB_INFO.label(), result.size() == 0 ? null : result);

        String userId = session.getId();
        List<Job2DepthInfo> interestResult = frontService.getInterestingJobs(userId);
        model.addAttribute(ModelField.JOB_INTEREST_INFO.label(), interestResult.size() == 0 ? null : interestResult);

        return "main-page";
    }

    @RequestMapping(value = {"/job-detail/{jobId}"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET})
    public String showJobDetail(Model model,
                                HttpSession session,
                                HttpServletRequest request,
                                @PathVariable(value = "jobId", required = true) String jobId) {
        String userAgent = request.getHeader("user-agent");
        boolean ignoreUser = userAgent == null ? false : userAgent.toLowerCase().contains("googlebot") ? true : false;

        String userId = session.getId();
        Map<String, String> detailResult = frontService.getJobDetail(jobId, userId, ignoreUser);
        model.addAttribute(ModelField.JOB_DETAIL_INFO.label(), detailResult.size() == 0 ? null : detailResult);

        List<Job2DepthInfo> interestResult = frontService.getInterestingJobs(userId);
        model.addAttribute(ModelField.JOB_INTEREST_INFO.label(), interestResult.size() == 0 ? null : interestResult);

        List<Job2DepthInfo> similarResult = frontService.getSimilarJobs(jobId);
        model.addAttribute(ModelField.JOB_SIMILAR_INFO.label(), similarResult.size() == 0 ? null : similarResult);

        return "detail-page";
    }

    @RequestMapping(value = {"/search-jobs"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET})
    public String searchJobs(Model model,
                             @RequestParam(value = "keywords") String keywords) {
        List<Job2DepthInfo> result = frontService.searchJobs(keywords);
        model.addAttribute(ModelField.JOB_INFO.label(), result.size() == 0 ? null : result);
        return "search-page";
    }
}
