package com.tn76.BusTicketBooking.repo;

import com.tn76.BusTicketBooking.entity.Bus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * BusRepo.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.tn76.BusTicketBooking.repo
 * @created Jun 09, 2023
 */

public interface BusRepo extends CrudRepository<Bus,Integer> {
}
