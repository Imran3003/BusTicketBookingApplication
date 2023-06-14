package com.tn76.BusTicketBooking.entity.user;


public class MyTickets
{
    private int busNo;
    private int seatNo;

    public int getBusNo() {
        return busNo;
    }

    public void setBusNo(int busNo) {
        this.busNo = busNo;
    }

    public int getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(int seatNo) {
        this.seatNo = seatNo;
    }

    @Override
    public String toString() {
        return "MyTickets{" +
                "busNo=" + busNo +
                ", seatNo=" + seatNo +
                '}';
    }
}
