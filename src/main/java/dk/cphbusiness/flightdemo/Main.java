package dk.cphbusiness.flightdemo;

import dk.cphbusiness.flightdemo.services.FlightService;
// Koden er taget fra Thomas
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        FlightService flightService = new FlightService("flights.json");

        flightService.totalFlightTimeByAirline("Lufthansa");
        flightService.averageFlightTimeByAirline("Lufthansa");
        flightService.listFlightsByDepartureAndArrival("Fukuoka", "Haneda");
        flightService.listFlightsBeforeTime(LocalDateTime.parse("2024-08-15T01:00"));
        flightService.averageFlightTimePerAirline();
        flightService.allFlightsSortedByArrivalTime();
        flightService.totalFlightTimePerAirline();
        flightService.allFlightsSortedByDuration();
    }
}