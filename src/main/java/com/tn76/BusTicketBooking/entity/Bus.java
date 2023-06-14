package com.tn76.BusTicketBooking.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Bus {
    @Id
    private Integer busNo;
    private String busName;
    private String busReachingTime;
    private String busStartingTime;
    private String startingFrom;
    private String destination;
    private  int availableSeat;
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "busNO")
    private List<Bookings> bookings;

    public Integer getBusNo() {
        return busNo;
    }

    public void setBusNo(Integer busNo) {
        this.busNo = busNo;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusReachingTime() {
        return busReachingTime;
    }

    public void setBusReachingTime(String busReachingTime) {
        this.busReachingTime = busReachingTime;
    }

    public String getBusStartingTime() {
        return busStartingTime;
    }

    public void setBusStartingTime(String busStartingTime) {
        this.busStartingTime = busStartingTime;
    }

    public String getStartingFrom() {
        return startingFrom;
    }

    public void setStartingFrom(String startingFrom) {
        this.startingFrom = startingFrom;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<Bookings> getBookings() {
        return bookings;
    }

    public void setBookings(List<Bookings> bookings) {
        this.bookings = bookings;
    }

    public int getAvailableSeat() {
        return availableSeat;
    }

    public void setAvailableSeat(int availableSeat) {
        this.availableSeat = availableSeat;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "busNo=" + busNo +
                ", busName='" + busName + '\'' +
                ", busReachingTime='" + busReachingTime + '\'' +
                ", busStartingTime='" + busStartingTime + '\'' +
                ", startingFrom='" + startingFrom + '\'' +
                ", destination='" + destination + '\'' +
                ", availableSeat=" + availableSeat +
                ", bookings=" + bookings +
                '}';
    }

    // Rest of the class code
}
