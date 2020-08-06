package com.softserve.marathon.controller;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.service.MarathonService;
import com.softserve.marathon.model.Marathon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/marathons")
public class MarathonController {
    Logger logger = LoggerFactory.getLogger(MarathonController.class);

    private final MarathonService marathonService;

    public MarathonController(MarathonService marathonService) {
        this.marathonService = marathonService;
    }

    @GetMapping
    public String showMarathons(Model model) {
        logger.info("Rendering marathon/list.html view");
        model.addAttribute("marathons", marathonService.getAll());
        return "marathon/list";
    }

    @GetMapping("/add")
    public String createMarathon(Model model) {
        logger.info("Rendering marathon/create.html view");
        model.addAttribute("marathon", new Marathon());
        return "marathon/create";
    }

    @PostMapping("/add")
    public String createMarathon(@Valid @ModelAttribute Marathon marathon,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) creating marathon" + bindingResult.getAllErrors());
            return "marathon/create";
        }
        logger.info("Creating new marathon");
        marathonService.createOrUpdateMarathon(marathon);
        return "redirect:/marathons";
    }


    @GetMapping("/edit/{id}")
    public String editMarathon(Model model, @PathVariable Long id) {
        model.addAttribute("marathon", marathonService.getMarathonById(id));
        return "marathon/edit";
    }

    @PostMapping("/edit/{id}")
    public String editMarathon(@Valid @ModelAttribute Marathon marathon,
                               BindingResult bindingResult,
                               @PathVariable Long id) {
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) editing marathon Id " + id + bindingResult.getAllErrors());
            return "marathon/edit";
        }
        logger.info("Editing marathon Id " + id);
        marathonService.createOrUpdateMarathon(marathon);
        return "redirect:/marathons";
    }

    @GetMapping("/delete/{id}")
    public String close(@PathVariable Long id) {
        logger.info("Close marathon");
        try {
            Marathon marathonFromDb = marathonService.getMarathonById(id);
            marathonFromDb.setClosed(true);
            marathonService.createOrUpdateMarathon(marathonFromDb);
        } catch (EntityNotFoundException e) {
            logger.error("Exception:" + e);
            e.printStackTrace();
        }
        return "redirect:/marathons";
    }
}


