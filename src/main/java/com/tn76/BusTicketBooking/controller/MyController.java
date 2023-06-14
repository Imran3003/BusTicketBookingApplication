package com.tn76.BusTicketBooking.controller;

import com.atm.scanAndcash.impl.ScanAndCashImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.tn76.BusTicketBooking.entity.Bookings;
import com.tn76.BusTicketBooking.entity.Bus;
import com.tn76.BusTicketBooking.entity.PassengerDetails;
import com.tn76.BusTicketBooking.entity.user.BookingTicketList;
import com.tn76.BusTicketBooking.entity.user.MyTickets;
import com.tn76.BusTicketBooking.entity.user.UserDetails;
import com.tn76.BusTicketBooking.repo.BusRepo;
import com.tn76.BusTicketBooking.repo.PassengerRepo;
import com.tn76.BusTicketBooking.repo.UserRepo;
import com.tn76.BusTicketBooking.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * LogInPageController.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.tn76.BusTicketBooking.controller
 * @created Jun 09, 2023
 */
@Controller
public class MyController
{
    @Autowired
    AddNewBus addNewBus;
    @Autowired
    UserRepo userRepo;

    @Autowired
    BusRepo busRepo;

    @Autowired
    PassengerRepo passengerRepo;

    String userName = "";

    int busNO = 0;

    private static final int TICKET_PRICE = 500;

    Map<String,String> bookingTickets = new HashMap<>();
    @RequestMapping("/")
    public ModelAndView bookingHome()
    {
        return new ModelAndView("bookingHome");
    }

    @RequestMapping("/showBusses")
    public ModelAndView showBusses(Model model)
    {

        List<Bus> allBuses = (List<Bus>) busRepo.findAll();
        model.addAttribute("busses",allBuses);

        return new ModelAndView("Busses.html");
    }
    @RequestMapping("/register")
    public ModelAndView registerHome()
    {
        return new ModelAndView("bookingRegister");
    }

    @RequestMapping("/createUser")
    public ModelAndView register(@RequestParam String name, @RequestParam String place ,
                                 @RequestParam String email, @RequestParam String phoneNo,
                                 @RequestParam String userName, @RequestParam String password, Model model)
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

        UserDetails userDetails = new UserDetails();
        userDetails.setName(name);
        userDetails.setUserName(userName);
        userDetails.setEmail(email);
        userDetails.setPassword(password);
        userDetails.setPhoneNo(phoneNo);
        userDetails.setPlace(place);

        userRepo.save(userDetails);
        modelAndView.setViewName("succefullyRegister");
        return modelAndView;

    }

    @RequestMapping("/login")
    public ModelAndView login(@RequestParam String username, @RequestParam String password, Model model)
    {
        ModelAndView modelAndView = new ModelAndView();

        StringBuilder buttonHtml = new StringBuilder();

        List<UserDetails> admins = (List<UserDetails>) userRepo.findAll();

        boolean userValid = false;

        for (UserDetails user : admins)
        {
            if (user.getUserName().equals(username))
            {
                if (user.getPassword().equals(password))
                    userValid = true;
            }
        }
        if (!userValid)
        {
            model.addAttribute("errorMessage","Please enter valid Username or password.");
            modelAndView.setViewName("bookingHome.html");
            return modelAndView;
        }

        List<Bus> allBuses = (List<Bus>) busRepo.findAll();
        System.out.println("allBuses = " + allBuses);
//        model.addAttribute("busses",allBuses);
        userName = username;
        model.addAttribute("busses",allBuses);
        model.addAttribute("username",username);
        if (username.equals("superuser") && password.equals("Superadmin@00"))
        {
            buttonHtml.append("<a class=\"btn1\"\" href=\"/createNewBusPage\">CreateBus</a>"); //createNewBus
            model.addAttribute("buttonHtml",buttonHtml);
        }
        modelAndView.setViewName("Busses.html");

        return modelAndView;
    }
    @RequestMapping("/selectedBus")
    public ModelAndView bookSelectedBus(@RequestParam("busNo") Integer busNo, Model model)
    {
        List<Bus> busses = (List<Bus>) busRepo.findAll();

        ModelAndView modelAndView  = new ModelAndView();

        StringBuilder buttonHtml = new StringBuilder();

        StringBuilder buttonHtml1 = new StringBuilder();

        for (Bus buss : busses) {
            if (Objects.equals(buss.getBusNo(), busNo))
            {
                List<Bookings> seats = buss.getBookings();
                for (int i = 0; i < seats.size(); i++) {
                    Bookings seat = seats.get(i);
                    int seatVal = i+1;
                    if (i < 20)
                    {
                        if (seat.getBookingStatus().equals(Constants.BookingStatus.BOOKED))
                            buttonHtml.append("<button class=\"myButton\" onclick=\"changeColor(this)\" data-value = ").append(seatVal).append("th:style=\"${buttonStyle} style=\"background-color: red;\"disabled = true value = " +seatVal+" \" >").append(seatVal).append("</button>\n");
                        else
                            buttonHtml.append("<button class=\"myButton\" onclick=\"changeColor(this)\" data-value = ").append(seatVal).append("th:style=\"${buttonStyle} \" value = " +seatVal+" \" >").append(seatVal).append("</button>\n");
                    }
                    else
                    {
                        if (seat.getBookingStatus().equals(Constants.BookingStatus.BOOKED))
                            buttonHtml1.append("<button class=\"myButton\" onclick=\"changeColor(this)\" data-value = ").append(seatVal).append("th:style=\"${buttonStyle} style=\"background-color: red;\"disabled = true value = " +seatVal+" \" >").append(seatVal).append("</button>\n");
                        else
                            buttonHtml1.append("<button class=\"myButton\" onclick=\"changeColor(this)\" data-value = ").append(seatVal).append("th:style=\"${buttonStyle}\" value = " +seatVal+" \" >").append(seatVal).append("</button>\n");
                    }
                }
                model.addAttribute("busno",busNo);
                model.addAttribute("buttonHtml", buttonHtml.toString());
                model.addAttribute("buttonHtml1", buttonHtml1.toString());
            }
                modelAndView.setViewName("sample.html");
            }
        return modelAndView;
    }

    @PostMapping("/submitMapData")
    public ModelAndView submitMapData(@RequestBody String json, Model model) throws IOException, WriterException {
        // Handle the received map data
        //
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> map = objectMapper.readValue(json, Map.class);

        System.out.println("map = " + map);

        bookingTickets = map;
        busNO = Integer.parseInt( map.get("busNo"));
        map.remove("busNo");
        System.out.println("map = " + map);
        System.out.println("busNO = " + busNO);

        int amount = TICKET_PRICE * map.size();
        System.out.println("amount = " + amount);

        String qrCodeData = ScanAndCashImpl.generateQRCode(amount);

        model.addAttribute("qrCodeData", qrCodeData);

        model.addAttribute("amount",amount);

        return new ModelAndView("qrCodePage", model.asMap());
        //return "redirect:/success-page"; // Redirect to a success page after processing the data
    }

    @RequestMapping("/successfullyTicketBooked")
    public ModelAndView ticketBooked(Model model)
    {
        String tickets = "";
        List<Bus> allBusses = (List<Bus>) busRepo.findAll();

        for (Bus bus : allBusses) {
            if (bus.getBusNo() == busNO)
            {
                int seatAvail = bus.getAvailableSeat();
                List<Bookings> bookings = bus.getBookings();
                for (Bookings booking : bookings) {
                    if (bookingTickets.containsKey(String.valueOf( booking.getSeatNo())))
                    {
                        booking.setBookingStatus(Constants.BookingStatus.BOOKED);
                        tickets = tickets + booking.getSeatNo() +",";
                        seatAvail --;
                    }
                }
                bus.setAvailableSeat(seatAvail);
            }
        }

        busRepo.saveAll(allBusses);
        savePassenger();
        String modifiedTickets = tickets.replaceAll(",\\s*$", "");
        model.addAttribute("tickets",modifiedTickets);
        return new ModelAndView("succefullyPaid");
    }

    private void savePassenger()
    {
        List<PassengerDetails> passengerDetailsList = new ArrayList<>();
        bookingTickets.forEach((key,value)->
        {
            PassengerDetails passengerDetails = new PassengerDetails();
            passengerDetails.setName(userName);
            passengerDetails.setBusNo(busNO);
            passengerDetails.setSeatNo(Integer.parseInt(key));
            passengerDetailsList.add(passengerDetails);
        });
        System.out.println("passengerDetailsList = " + passengerDetailsList);
        passengerRepo.saveAll(passengerDetailsList);
    }


    //Create a new Bus

    @RequestMapping("/createNewBusPage")
    public ModelAndView createBusPage()
    {
        return new ModelAndView("createbus");
    }
    @RequestMapping("/createNewBus")
    public ModelAndView createNewBus(@RequestParam String busName,@RequestParam int busNum,
                                  @RequestParam String startingFrom,@RequestParam String destination,
                                  @RequestParam String startingTime,@RequestParam String reachingTime,
                                  @RequestParam int noOfSeats, Model model)
    {

        ModelAndView modelAndView = new ModelAndView();
        Bus bus = new Bus();
        bus.setBusName(busName);
        bus.setBusNo(busNum);
        bus.setStartingFrom(startingFrom);
        bus.setBusStartingTime(startingTime);
        bus.setDestination(destination);
        bus.setBusReachingTime(reachingTime);


        List<Bookings> bookingsList = new ArrayList<>();
        for (int i = 1 ; i <= noOfSeats ; i++)
        {
            Bookings bookings = new Bookings();
            bookings.setSeatNo(i);
            bookings.setBookingStatus(Constants.BookingStatus.AVAILABLE);
            bookingsList.add(bookings);
        }
        bus.setBookings(bookingsList);
        bus.setAvailableSeat(noOfSeats);
        model.addAttribute("busNum",busNum);
        busRepo.save(bus);
        modelAndView.setViewName("successfullyCreateNewBus.html");
        return modelAndView;
    }

    private void createBus()
    {
        createNewBus("Hail Trip",8663,"Tenkasi","Chennai","09-Jun-2023 : 7.00 PM","10-Jun-2023 : 6.30 AM",40,null);
        createNewBus("JollyBus",1967,"Chennai","Tenkasi","10-Jun-2023 : 5.00 PM","11-Jun-2023 : 5.30 AM",40,null);

    }

    @RequestMapping("/showMyTickets")
    public ModelAndView showMyTickets(@RequestParam("userName") String  username, Model model)
//    public void showMyTickets(@RequestParam("busNo") Integer busNo)
    {
        System.out.println(" Inside showMyTickets");
        System.out.println("username = " + username);

        List<MyTickets> myTicketList = new ArrayList<>();
        List<BookingTicketList> bookingTicketLists = new ArrayList<>();

        List<PassengerDetails> passengers = (List<PassengerDetails>) passengerRepo.findAll();

        for (PassengerDetails passenger : passengers) {
            if (passenger.getName().equals(userName))
            {
                MyTickets myTickets = new MyTickets();
                myTickets.setBusNo(passenger.getBusNo());
                myTickets.setSeatNo(passenger.getSeatNo());
                myTicketList.add(myTickets);
            }
        }

        Map<Integer, List<Integer>> groupedTickets = myTicketList.stream()
                .collect(Collectors.groupingBy(MyTickets::getBusNo,
                        Collectors.mapping(MyTickets::getSeatNo, Collectors.toList())));
        List<Bus> allBusses = (List<Bus>) busRepo.findAll();
        for (Bus bus : allBusses) {
            if (groupedTickets.containsKey(bus.getBusNo()))
            {
                List<Integer> tickets = groupedTickets.get(bus.getBusNo());
                for (int i = 0 ; i< tickets.size();i++)
                {
                    BookingTicketList bookingTicketList = new BookingTicketList();
                    bookingTicketList.setBusName(bus.getBusName());
                    bookingTicketList.setBusNo(bus.getBusNo());
                    bookingTicketList.setStartingFrom(bus.getStartingFrom());
                    bookingTicketList.setDestination(bus.getDestination());
                    bookingTicketList.setBusStartingTime(bus.getBusStartingTime());
                    bookingTicketList.setSeatNo(tickets.get(i));
                    bookingTicketLists.add(bookingTicketList);
                    bookingTicketList.setPname(userName);
                }
            }
        }
        model.addAttribute("busses",bookingTicketLists);

        return new ModelAndView("CancelPage.html");
    }

    @RequestMapping("/cancelTicket")
     public ModelAndView cancelTicket(@RequestParam("busNo") int busNum, @RequestParam("seatNo") int seatNo,@RequestParam("passengerName") String passengerName)    {
        List<Bus> all = (List<Bus>) busRepo.findAll();
        for (Bus bus : all) {
            int seatAvail = bus.getAvailableSeat();
            if (bus.getBusNo() == busNum)
            {
                List<Bookings> bookings = bus.getBookings();
                for (Bookings booking : bookings) {
                    if (seatNo == booking.getSeatNo())
                    {
                        booking.setBookingStatus(Constants.BookingStatus.CANCELED);
                        seatAvail ++;
                        bus.setAvailableSeat(seatAvail);
                        List<PassengerDetails> allPassengers = (List<PassengerDetails>) passengerRepo.findAll();
                        for (PassengerDetails passenger : allPassengers) {
                            if (passenger.getName().equals(userName) && passenger.getBusNo() == busNum && passenger.getSeatNo() == seatNo)
                            {
                                passengerRepo.delete(passenger);
                            }
                        }
                    }
                }
            }
        }
        busRepo.saveAll(all);
        return new ModelAndView("succefullyCanceled.html");
    }
}
