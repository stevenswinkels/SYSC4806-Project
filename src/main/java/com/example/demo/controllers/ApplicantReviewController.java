package com.example.demo.controllers;

import com.example.demo.Verdict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ApplicantReviewController {


    @GetMapping("/evaluations/{profID}/")
    String evaluations(@PathVariable("profID") String profID, Model model) {
        /* TODO
        * Retrieve the applications the prof needs to evaluate
        * */

        return "evaluations";
    }

    @GetMapping("/evaluations/{profID}/{applicationID}")
    String evaluateApplications(@PathVariable("profID") String profID,
                                @PathVariable("applicationID") String applicationID,
                                Model model) {
        /* TODO
         * Retrieve a specific application
         * */

        model.addAttribute("decisions", Verdict.values());

        return "applications";
    }


    @PostMapping("evaluations/{profID}/{applicationID}")
    String postEvaluateApplications(@PathVariable("profID") String profID,
                                    @PathVariable("applicationID") String applicationID,
                                    @RequestParam(name = "decision", required = true) String decision){
        try {
            Verdict verdict = Verdict.valueOf(decision);
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }

        /* TODO
        * apply decision to DB/Model
         */

        System.out.println("decision: " + decision);

        return "redirect:/evaluations/{profID}/";
    }
}
