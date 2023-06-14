package com.tn76.BusTicketBooking.samples;

import java.util.HashMap;
import java.util.Map;

public class example
{
    public static void main(String[] args) {
        Map<Integer,String> map = new HashMap<>();
        map.put(1,"seatNo1");
        map.put(2,"seatNo2");
        System.out.println("map = " + map);

        int seat = 1;

        if (map.containsKey(seat))
            System.out.println("true");
        else
            System.out.println("false");
    }
}
