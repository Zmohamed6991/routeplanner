package com.hstc.routeplanner.service;

import com.hstc.routeplanner.model.TransportResponse;
import org.springframework.stereotype.Service;

@Service
public class TransportService {
    private static final double PERSONAL_TRANSPORT_RATE = 0.30;
    private static final double HSTC_TRANSPORT_RATE = 0.45;
    private static final double PARKING_RATE = 5.0;

    public TransportResponse calculateTransport(double distance, int passengers, int parkingDays) {
        double personalTransportCost = (PERSONAL_TRANSPORT_RATE * distance) + (parkingDays * PARKING_RATE);
        double hstcTransportCost = HSTC_TRANSPORT_RATE * distance;

        if (passengers > 4) {
            return new TransportResponse("HSTC Transport", hstcTransportCost);
        }

        return new TransportResponse("Personal Transport", personalTransportCost);
    }
}