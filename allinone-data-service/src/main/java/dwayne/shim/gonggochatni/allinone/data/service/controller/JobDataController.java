package dwayne.shim.gonggochatni.allinone.data.service.controller;

import dwayne.shim.gonggochatni.allinone.data.service.service.JobDataService;
import dwayne.shim.gonggochatni.allinone.data.service.service.UserPreferenceDataService;
import dwayne.shim.gonggochatni.common.data.JobData;
import dwayne.shim.gonggochatni.common.indexing.JobDataIndexField;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/job")
public class JobDataController {

    @Resource
    private JobDataService jobDataService;

    @Resource
    private UserPreferenceDataService userPreferenceDataService;

    @RequestMapping(value = {"/new"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET})
    public ResponseEntity<List<JobData>> getNewJobs() {
        List<JobData> result;
        try {
            result = jobDataService.getNewJobs();
        } catch (Exception e) {
            result = new ArrayList<>();
        }

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = {"/search"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<List<JobData>> searchJobs(@RequestParam(value = "keywords", required = true) String keywords) {
        List<JobData> result;
        try {
            result = jobDataService.searchJobs(keywords);
        } catch (Exception e) {
            result = new ArrayList<>();
        }

        return new ResponseEntity(result, HttpStatus.OK);
    }

    private final JobDataIndexField[] fieldsForUserKeywords = {
            JobDataIndexField.KEYWORD,
            JobDataIndexField.POSITION_TITLE_KEYWORDS,
            JobDataIndexField.COMPANY_NAME,
            JobDataIndexField.POSITION_LOCATION
    };

    @RequestMapping(value = {"/detail/{jobId}"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET})
    public ResponseEntity<JobData> getJobDetail(@PathVariable(value = "jobId") String jobId,
                                                @RequestParam(value = "userId", required = false) String userId,
                                                @RequestParam(value = "ignoreUser", required = false, defaultValue = "false") boolean ignoreUser) {

        JobData jobData = null;
        try {
            // 1. get job detail data ...
            jobData = jobDataService.getJobDetail(jobId);

            // 2. register user-keywords info ...
            if(userId != null && !ignoreUser) userPreferenceDataService.addUserKeywords(userId, jobData.getInfoMap(), fieldsForUserKeywords);
        } catch (Exception e) {
            jobData = JobData.dummyJobData();
        }
        return new ResponseEntity(jobData, HttpStatus.OK);
    }

    @RequestMapping(value = {"/interest/{userId}"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET})
    public ResponseEntity<List<JobData>> getInterestingJobs(@PathVariable(value = "userId") String userId) {

        List<JobData> result;
        try {
            String userKeywords = userPreferenceDataService.getUserKeywords(userId);
            if(userKeywords == null) throw new NullPointerException();

            result = jobDataService.interestingLocations(userKeywords);
        } catch (Exception e) {
            result = new ArrayList<>();
        }

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = {"/similar/{jobId}"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET})
    public ResponseEntity<List<JobData>> getSimilarJobs(@PathVariable(value = "jobId") String jobId) {

        List<JobData> result;
        try {
            result = jobDataService.getSimilarJobs(jobId);
        } catch (Exception e) {
            result = new ArrayList<>();
        }

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @Scheduled(fixedRateString = "${user.keywords.ttl-check.interval}", initialDelayString = "${user.keywords.ttl-check.init-time}")
    private void checkTTLofUserKeywords() {
        log.info("Start checking user-keywords ttl ...");
        try {
            userPreferenceDataService.removeOldUserData();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        log.info("Finished checking user-keywords ttl ...");
    }
}
