package com.tn76.BusTicketBooking.samples;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
    @GetMapping("/mainPage")
    public ModelAndView mainPage() {
        return new ModelAndView("mainpage");
    }
}
