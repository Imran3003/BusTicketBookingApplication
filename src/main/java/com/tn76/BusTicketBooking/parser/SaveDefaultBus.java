package com.tn76.BusTicketBooking.parser;

import com.tn76.BusTicketBooking.entity.Bus;
import com.tn76.BusTicketBooking.repo.BusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SaveDefaultBus.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.tn76.BusTicketBooking.parser
 * @created Jun 26, 2023
 */

@Component
public class SaveDefaultBus
{
    @Autowired
    BusRepo busRepo;

    public void SaveBus(Bus bus)
    {
        busRepo.save(bus);
    }

}
