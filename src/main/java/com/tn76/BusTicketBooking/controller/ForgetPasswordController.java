package com.tn76.BusTicketBooking.controller;

import com.tn76.BusTicketBooking.Email.EmailSender;
import com.tn76.BusTicketBooking.entity.user.UserDetails;
import com.tn76.BusTicketBooking.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * ForgetPasswordController.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.tn76.BusTicketBooking.controller
 * @created Sep 15, 2023
 */

@Controller
public class ForgetPasswordController
{
    @Autowired
    UserRepo userRepo;

    @RequestMapping("/forgetPasswordHome")
    public ModelAndView forgetPassword()
    {
        return new ModelAndView("forgetPasswordPage");
    }

    @RequestMapping("/forgetPassword")
    public ModelAndView sendPasswordToEmail(@RequestParam String userDetail, Model model)
    {
        List<UserDetails> users = (List<UserDetails>) userRepo.findAll();

        ModelAndView modelAndView = new ModelAndView();

        for (UserDetails user : users)
        {
            if (user.getUserName().equals(userDetail) || user.getEmail().equals(userDetail))
            {
                List<String> texts = new ArrayList<>();
                texts.add("UserName : " + user.getUserName());
                texts.add("Password : " + user.getPassword());
                EmailSender.sendEmail(user.getEmail(),"Restored Password",texts);

                model.addAttribute("message","password send to your registered email.");
                modelAndView.setViewName("forgetpasswordResetPage");
                return modelAndView;
            }

        }
        model.addAttribute("message","No user found for " + userDetail + "Please register.");
        modelAndView.setViewName("forgetPasswordPage");

        return modelAndView;
    }
}
