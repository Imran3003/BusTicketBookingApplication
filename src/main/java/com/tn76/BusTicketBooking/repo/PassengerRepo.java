package com.tn76.BusTicketBooking.repo;

import com.tn76.BusTicketBooking.entity.PassengerDetails;
import org.springframework.data.relational.core.sql.In;
import org.springframework.data.repository.CrudRepository;

public interface PassengerRepo extends CrudRepository<PassengerDetails, Long> {
}
