package com.tn76.BusTicketBooking.repo;

import com.tn76.BusTicketBooking.entity.user.UserDetails;
import org.springframework.data.repository.CrudRepository;

/**
 * UserRepo.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.tn76.BusTicketBooking.repo
 * @created Jun 09, 2023
 */
public interface UserRepo extends CrudRepository<UserDetails,Long> {
}
