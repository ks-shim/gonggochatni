package dwayne.shim.gonggochatni.front.service.controller;

import dwayne.shim.gonggochatni.front.service.constants.ModelField;
import dwayne.shim.gonggochatni.front.service.model.Job2DepthInfo;
import dwayne.shim.gonggochatni.front.service.service.FrontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

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

        /*String userId = session.getId();
        List<Destination2DepthInfo> interestResult = frontService.getInterestingDestinations(userId);
        model.addAttribute(ModelField.DESTINATION_INTEREST_INFO.label(), interestResult.size() == 0 ? null : interestResult);*/

        return "main-page";
    }
}
