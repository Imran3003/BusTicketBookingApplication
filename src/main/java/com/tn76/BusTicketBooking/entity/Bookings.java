package com.tn76.BusTicketBooking.entity;

import com.tn76.BusTicketBooking.utils.Constants;

import javax.persistence.*;

/**
 * Bookings.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.tn76.BusTicketBooking.entity
 * @created Jun 09, 2023
 */
@Entity
public class Bookings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    private int seatNo;
    @Enumerated(EnumType.STRING)
    private Constants.BookingStatus bookingStatus;

    public int getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(int seatNo) {
        this.seatNo = seatNo;
    }

    public Constants.BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(Constants.BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    @Override
    public String toString() {
        return "Bookings{" +
                "id=" + id +
                ", seatNo=" + seatNo +
                ", bookingStatus=" + bookingStatus +
                '}';
    }
}
