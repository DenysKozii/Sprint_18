package com.softserve.marathon.controller;

import com.softserve.marathon.model.Sprint;
import com.softserve.marathon.service.SprintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/sprints")
public class SprintController {
    Logger logger = LoggerFactory.getLogger(SprintController.class);

    SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @GetMapping("/marathon/{marathonId}")
    public String SprintListByMarathon(@PathVariable Long marathonId, Model model) {
        List<Sprint> sprints = sprintService.getSprintsByMarathon(marathonId);
        model.addAttribute("sprints", sprints);
        logger.info("Rendering sprint/list.html view");
        return "sprint/list";
    }
}
