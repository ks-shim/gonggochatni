package dwayne.shim.gonggochatni.allinone.data.service.controller;

import dwayne.shim.gonggochatni.allinone.data.service.service.JobDataService;
import dwayne.shim.gonggochatni.common.data.JobData;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/job")
public class JobDataController {

    @Resource
    private JobDataService jobDataService;

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
}
