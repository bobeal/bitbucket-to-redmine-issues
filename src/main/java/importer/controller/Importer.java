package importer.controller;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import importer.service.BitbucketService;
import importer.service.RedmineService;

@Controller
public class Importer {

    private Log log = LogFactory.getLog(Importer.class);

    @Autowired
    private BitbucketService bitbucketService;

    @Autowired
    private RedmineService redmineService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/upload-issues", method = RequestMethod.POST)
    public String uploadIssues(@RequestParam("issuesFile") MultipartFile file, Model model) {
        if (!file.isEmpty()) {
            try {
                bitbucketService.initializeData(file.getBytes());
                return "redirect:/mapping";
            } catch (Exception e) {
                model.addAttribute("error", "Error parsing file");
                return "index";
            }
        } else {
            model.addAttribute("error", "Empty file");
            return "index";
        }
    }

    @RequestMapping(value = "/mapping", method = RequestMethod.GET)
    public String mapping(Model model) {
        model.addAttribute("bitbucketUsers", bitbucketService.listUsers());
        model.addAttribute("bitbucketIssuesStatuses", bitbucketService.listIssuesStatuses());
        try {
            model.addAttribute("redmineUsers", redmineService.listUsers());
            model.addAttribute("redmineIssuesStatuses", redmineService.listIssuesStatuses());
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving data from Redmine");
        }
        return "mapping";
    }

    @RequestMapping(value = "/mapping", method = RequestMethod.POST)
    public String handleMapping(@RequestParam Map<String, String> params) {
        log.info("Received body : " + params);
        Map<String, String> userMappings = parseParam(params, "user-");
        Map<String, String> statusMappings = parseParam(params, "status-");
        return "mapping";
    }

    private Map<String, String> parseParam(Map<String, String> params, String prefix) {
        return params.keySet().stream()
                .filter(param -> param.startsWith(prefix))
                .collect(Collectors.toMap(new Function<String, String>() {
                    public String apply(String input) { return input.replaceFirst(prefix, ""); }
                }, param -> params.get(param)));
    }
}
