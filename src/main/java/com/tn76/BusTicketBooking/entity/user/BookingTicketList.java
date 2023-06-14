package com.tn76.BusTicketBooking.entity.user;

public class BookingTicketList
{
    private int busNo;
    private int seatNo;
    private String busName;
    private String busStartingTime;
    private String startingFrom;
    private String destination;

    private String Pname;

    public String getPname() {
        return Pname;
    }

    public void setPname(String pname) {
        this.Pname = pname;
    }

    @Override
    public String toString() {
        return "BookingTicketList{" +
                "busNo=" + busNo +
                ", seatNo=" + seatNo +
                ", busName='" + busName + '\'' +
                ", busStartingTime='" + busStartingTime + '\'' +
                ", startingFrom='" + startingFrom + '\'' +
                ", destination='" + destination + '\'' +
                ", name='" + Pname + '\'' +
                '}';
    }

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

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
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
}
