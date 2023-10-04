package com.tn76.BusTicketBooking.controller;

import com.tn76.BusTicketBooking.Email.EmailSender;
import com.tn76.BusTicketBooking.entity.user.UserDetails;
import com.tn76.BusTicketBooking.otp.OTPGenerator;
import com.tn76.BusTicketBooking.repo.BusRepo;
import com.tn76.BusTicketBooking.repo.PassengerRepo;
import com.tn76.BusTicketBooking.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

/**
 * RegistrationController.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.tn76.BusTicketBooking.controller
 * @created Oct 03, 2023
 */
@Controller
public class RegistrationController
{
    @Autowired
    AddNewBus addNewBus;
    @Autowired
    UserRepo userRepo;

    @Autowired
    BusRepo busRepo;

    @Autowired
    PassengerRepo passengerRepo;

    @RequestMapping("/register")
    public ModelAndView registerHome()
    {
        return new ModelAndView("bookingRegister");
    }

    @RequestMapping("/createUser")
    public ModelAndView register(@RequestParam String name, @RequestParam String place ,
                                 @RequestParam String email, @RequestParam String phoneNo,
                                 @RequestParam String userName, @RequestParam String password, Model model , HttpSession session)
    {

        ModelAndView modelAndView = new ModelAndView();
        List<UserDetails> allUsers = (List<UserDetails>) userRepo.findAll();
        boolean userAlreadyRegister = false;
        for (UserDetails allUser : allUsers) {
            if (allUser.getUserName().equals(userName))
                userAlreadyRegister = true;
        }

        if (userAlreadyRegister)
        {
            model.addAttribute("errorMessage","UserName Already find please try another  userName");
            modelAndView.setViewName("bookingRegister");
            return modelAndView;
        }

        String otp = OTPGenerator.generateOTP();

        session.setAttribute("otp", otp);
        session.setAttribute("name", name);
        session.setAttribute("userName", userName);
        session.setAttribute("email", email);
        session.setAttribute("password", password);
        session.setAttribute("phoneNo", phoneNo);
        session.setAttribute("place", place);

        EmailSender.sendEmail(email,"MyBus OTP Verification", Collections.singletonList("OTP : " + otp));

        modelAndView.setViewName("otp.html");

        return modelAndView;

    }

    @GetMapping("/verifyOTP")
    public ModelAndView verifyOTP(@RequestParam("digit1") String digit1,
                                  @RequestParam("digit2") String digit2,
                                  @RequestParam("digit3") String digit3,
                                  @RequestParam("digit4") String digit4,
                                  HttpSession session, Model model) {
        ModelAndView modelAndView = new ModelAndView();

        // Retrieve the stored OTP and user details from the session
        String otp = (String) session.getAttribute("otp");
        String name = (String) session.getAttribute("name");
        String userName = (String) session.getAttribute("userName");
        String email = (String) session.getAttribute("email");
        String password = (String) session.getAttribute("password");
        String phoneNo = (String) session.getAttribute("phoneNo");
        String place = (String) session.getAttribute("place");

        String enteredOtp = digit1 + digit2 + digit3 + digit4;

        if (otp != null && otp.equals(enteredOtp)) {
            // OTP is correct, proceed to save the user details and redirect to the success page

            UserDetails userDetails = new UserDetails();
            userDetails.setName(name);
            userDetails.setUserName(userName);
            userDetails.setEmail(email);
            userDetails.setPassword(password);
            userDetails.setPhoneNo(phoneNo);
            userDetails.setPlace(place);

            userRepo.save(userDetails);

            modelAndView.setViewName("succefullyRegister");
        } else {
            // OTP is incorrect, show an error message
            model.addAttribute("errorMessage", "Incorrect OTP. Please try again.");
            modelAndView.setViewName("otp.html");
        }
        return modelAndView;
    }
}
