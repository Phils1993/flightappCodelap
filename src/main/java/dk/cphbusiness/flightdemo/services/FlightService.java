package dk.cphbusiness.flightdemo.services;


//Koden er taget fra Thomas

import dk.cphbusiness.flightdemo.FlightReader;
import dk.cphbusiness.flightdemo.dtos.FlightDTO;
import dk.cphbusiness.flightdemo.dtos.FlightInfoDTO;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * FlightService class
 * -------------------
 * This class provides various operations on a list of flights and their details.
 * It reads flight data from a source (usually a file) and converts them into
 * FlightInfoDTO objects for further analysis.
 * <p>
 * Features include:
 * - Calculating total and average flight time per airline
 * - Filtering flights by departure and arrival airports
 * - Listing flights before a specific time
 * - Sorting flights by arrival time or duration
 */
public class FlightService {

    // Original list of flights read from the data source
    List<FlightDTO> flights;

    // List of detailed flight info objects, derived from flights
    List<FlightInfoDTO> flightInfoList;

    /**
     * Constructor: initializes the service with flight data from a source file
     *
     * @param source Path to the data file containing flight information
     */
    public FlightService(String source) {
        try {
            // Read flight data from file
            this.flights = FlightReader.getFlightsFromFile(source);

            // Convert raw flight data into detailed flight info objects
            this.flightInfoList = FlightReader.getFlightInfoDetails(flights);
        } catch (IOException e) {
            // If reading the file fails, throw a runtime exception
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculates and prints the total flight time for a specific airline
     *
     * @param airline Name of the airline
     */
    public void totalFlightTimeByAirline(String airline) {
        Duration totalFlightTime = flightInfoList.stream()
                .filter(x -> x.getAirline() != null) // exclude flights with null airline
                .filter(x -> x.getAirline().equals(airline)) // match airline
                .map(FlightInfoDTO::getDuration) // get flight duration
                .filter(Objects::nonNull) // ignore null durations
                .reduce(Duration.ZERO, Duration::plus); // sum durations
        System.out.println(airline + ": " + totalFlightTime);
    }

    /**
     * Calculates and prints the average flight time for a specific airline
     *
     * @param airline Name of the airline
     */
    public void averageFlightTimeByAirline(String airline) {
        Double averageFlightTime = flightInfoList.stream()
                .filter(x -> x.getAirline() != null) // exclude null airlines
                .filter(x -> x.getAirline().equals(airline)) // match airline
                .map(x -> x.getDuration().toMinutes()) // convert durations to minutes
                .collect(Collectors.averagingLong(x -> x)); // calculate average
        System.out.println(airline + ": " + Duration.ofMinutes(averageFlightTime.longValue()));
    }

    /**
     * Lists all flights between two specific airports
     *
     * @param departureAirport Departure airport name
     * @param arrivalAirport   Arrival airport name
     */
    public void listFlightsByDepartureAndArrival(String departureAirport, String arrivalAirport) {
        flightInfoList.stream()
                .filter(x -> x.getOrigin() != null) // ensure origin is not null
                .filter(x -> x.getDestination() != null) // ensure destination is not null
                .filter(x -> x.getOrigin().contains(departureAirport)) // match departure
                .filter(x -> x.getDestination().contains(arrivalAirport)) // match arrival
                .forEach(System.out::println); // print each matching flight
    }

    /**
     * Lists all flights departing before a specified time
     *
     * @param dateTime LocalDateTime threshold
     */
    public void listFlightsBeforeTime(LocalDateTime dateTime) {
        flightInfoList.stream()
                .filter(x -> x.getDeparture().isBefore(dateTime)) // filter by departure
                .forEach(System.out::println); // print matching flights
    }

    /**
     * Calculates and prints the average flight time for each airline
     */
    public void averageFlightTimePerAirline() {
        flightInfoList.stream()
                .filter(x -> x.getAirline() != null)
                .collect(Collectors.groupingBy(
                        FlightInfoDTO::getAirline, // group by airline
                        Collectors.averagingLong(x -> x.getDuration().toMinutes()) // average duration
                ))
                .forEach((x, y) -> System.out.println(x + ": " + Duration.ofMinutes(y.longValue())));
    }

    /**
     * Lists all flights sorted by arrival time (earliest first)
     */
    public void allFlightsSortedByArrivalTime() {
        flightInfoList.stream()
                .sorted(Comparator.comparing(FlightInfoDTO::getArrival)) // sort by arrival
                .forEach(System.out::println); // print sorted flights
    }

    /**
     * Calculates and prints the total flight time for each airline
     */
    public void totalFlightTimePerAirline() {
        flightInfoList.stream()
                .filter(x -> x.getAirline() != null) // exclude null airlines
                .filter(x -> x.getDuration() != null) // exclude null durations
                .collect(Collectors.groupingBy(
                        FlightInfoDTO::getAirline, // group by airline
                        Collectors.summingLong(x -> x.getDuration().toMinutes()) // sum durations
                ))
                .forEach((x, y) -> System.out.println(x + ": " + Duration.ofMinutes(y)));
    }

    /**
     * Lists all flights sorted by duration (shortest first)
     */
    public void allFlightsSortedByDuration() {
        flightInfoList.stream()
                .sorted(Comparator.comparing(FlightInfoDTO::getDuration)) // sort by duration
                .forEach(System.out::println); // print sorted flights
    }
}
