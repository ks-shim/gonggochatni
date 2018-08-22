package dwayne.shim.gonggochatni.front.service.controller;

import dwayne.shim.gonggochatni.front.service.constants.ModelField;
import dwayne.shim.gonggochatni.front.service.service.FrontStatisticsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Controller
@RequestMapping("/")
public class FrontStatisticsController {

    @Resource
    private FrontStatisticsService statisticsService;

    @RequestMapping(value = {"/statistics"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET})
    public String showAreaStatistics(Model model) {

        // 4. get session-count data
        List<String> sessionLabelList = new ArrayList<>();
        List<Integer> sessionCountList = new ArrayList<>();
        try {
            statisticsService.getSessionCount(sessionLabelList, sessionCountList);
        } catch (Exception e) {
            log.error(e);
        }

        model.addAttribute(ModelField.STATISTICS_SESSION_COUNT_LABELS.label(), sessionLabelList);
        model.addAttribute(ModelField.STATISTICS_SESSION_COUNT_VALUES.label(), sessionCountList);

        return "statistics-page";
    }
}
