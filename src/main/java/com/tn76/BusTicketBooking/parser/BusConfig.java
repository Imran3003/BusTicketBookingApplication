package com.tn76.BusTicketBooking.parser;

import com.tn76.BusTicketBooking.entity.Bookings;
import com.tn76.BusTicketBooking.entity.Bus;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * BusConfig.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.tn76.BusTicketBooking.parser
 * @created Jun 26, 2023
 */
@Configuration
@ConfigurationProperties(prefix = "busses")
@PropertySource(value = "file:/home/admin/Downloads/BusTicketBooking/src/main/resources/DefaultBus.yml", factory = YamlPropertySourceFactory.class)
public class BusConfig
{
   Bus bus;

    @Override
    public String toString() {
        return "BusConfig{" +
                "bus=" + bus +
                '}';
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

}
