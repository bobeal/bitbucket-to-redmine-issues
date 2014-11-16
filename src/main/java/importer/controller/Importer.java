package importer.controller;

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
        try {
            model.addAttribute("redmineUsers", redmineService.listUsers());
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving data from Redmine");
        }
        return "mapping";
    }
}
