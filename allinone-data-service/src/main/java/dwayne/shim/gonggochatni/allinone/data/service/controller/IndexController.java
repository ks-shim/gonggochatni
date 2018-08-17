package dwayne.shim.gonggochatni.allinone.data.service.controller;

import dwayne.shim.gonggochatni.allinone.data.service.service.JobDataService;
import dwayne.shim.gonggochatni.allinone.data.service.util.IndexPathUtil;
import dwayne.shim.gonggochatni.common.indexing.JobDataIndexField;
import dwayne.shim.gonggochatni.searching.SearchingExecutor;
import lombok.extern.log4j.Log4j2;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/index")
public class IndexController {

    @Value("${job.index.path.file}")
    private String jobIndexPathFile;

    @Value("${job.index.dir1}")
    private String jobIndexDir1;

    @Value("${job.index.dir2}")
    private String jobIndexDir2;

    @Autowired
    private IndexPathUtil indexPathUtil;

    @Autowired
    private SearchingExecutor searchingExecutor;

    @Resource
    private JobDataService jobDataService;

    @RequestMapping(value = {"/to-be-path"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET})
    public String getToBeIndexPath() {
        try {
            return indexPathUtil.getNextIndexPath(jobIndexPathFile, jobIndexDir1, jobIndexDir2);
        } catch (Exception e) {
            return "";
        }
    }

    @RequestMapping(value = {"/switch-path"}, produces = "application/json; charset=utf8", method = {RequestMethod.PUT})
    public void forceToSwitchIndexDir() {
        try {
            String toBeIndexPath = indexPathUtil.getNextIndexPath(jobIndexPathFile, jobIndexDir1, jobIndexDir2);
            searchingExecutor.switchIndexLocation(toBeIndexPath);
            log.info("Switched index path ...");
            indexPathUtil.switchIndexPath(jobIndexPathFile, toBeIndexPath);
            log.info("Switched index path info in the file ...");
        } catch (Exception e) {
            log.error(e);
        }
    }

    @RequestMapping(value = {"/do-incremental-indexing"}, produces = "application/json; charset=utf8", method = {RequestMethod.POST})
    public void doIncrementalResumeIndexing(@RequestBody List<Map<String, String>> docList) throws Exception {
        searchingExecutor.updateDocuments(docList, JobDataIndexField.ID.label());
    }
}