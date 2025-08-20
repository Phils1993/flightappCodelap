package dk.cphbusiness.flightdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cphbusiness.flightdemo.dtos.ArrivalDTO;
import dk.cphbusiness.flightdemo.dtos.DepartureDTO;
import dk.cphbusiness.flightdemo.dtos.FlightDTO;
import dk.cphbusiness.flightdemo.dtos.FlightInfoDTO;
import dk.cphbusiness.utils.Utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class FlightReader {

    public static void main(String[] args) {
        try {
            List<FlightDTO> flightList = getFlightsFromFile("flights.json");
            List<FlightInfoDTO> flightInfoDTOList = getFlightInfoDetails(flightList);
            //flightInfoDTOList.forEach(System.out::println);
            double totalLufthansa = getTotalFlightTimeForAirline(flightList,"Lufthansa");
            System.out.println("Total flight time for: " + totalLufthansa);
            double averageLufthansa = getAverageFlightTimeForAirline(flightList,"Lufthansa");
            System.out.println("Average time for Lufthansa: " + averageLufthansa);
            List<FlightDTO> flightsBetweenAirport = flightsBetweenAirports(flightList,"Queen Alia International","Halle");
            System.out.println("Total Flights Between Fukuoka and Haneda Airport: " + flightsBetweenAirport.size());
            // All flights that leave before 01:00 (1 AM)
            List<FlightInfoDTO> nightFlights =
                    getFlightsBeforeSpecificTime(flightList, LocalTime.of(1, 0));
            System.out.println("Total Flights Before: " + nightFlights);

            double aveTimeForAll = calcAverageFlightTime(flightList);
            System.out.println("Average time for All Airlines: " + aveTimeForAll);




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<FlightDTO> getFlightsFromFile(String filename) throws IOException {

        ObjectMapper objectMapper = Utils.getObjectMapper();

        // Deserialize JSON from a file into FlightDTO[]
        FlightDTO[] flightsArray = objectMapper.readValue(Paths.get("flights.json").toFile(), FlightDTO[].class);

        // Convert to a list
        List<FlightDTO> flightsList = List.of(flightsArray);
        return flightsList;
    }

    public static List<FlightInfoDTO> getFlightInfoDetails(List<FlightDTO> flightList) {
        List<FlightInfoDTO> flightInfoList = flightList.stream()
           .map(flight -> {
                LocalDateTime departure = flight.getDeparture().getScheduled();
                LocalDateTime arrival = flight.getArrival().getScheduled();
                Duration duration = Duration.between(departure, arrival);
                FlightInfoDTO flightInfo =
                        FlightInfoDTO.builder()
                            .name(flight.getFlight().getNumber())
                            .iata(flight.getFlight().getIata())
                            .airline(flight.getAirline().getName())
                            .duration(duration)
                            .departure(departure)
                            .arrival(arrival)
                            .origin(flight.getDeparture().getAirport())
                            .destination(flight.getArrival().getAirport())
                            .build();

                return flightInfo;
            })
        .toList();
        return flightInfoList;
    }

    public static double getTotalFlightTimeForAirline(List<FlightDTO> flightList, String airlineName) {
        return flightList.stream()
                .filter(f -> f.getAirline() != null && airlineName.equalsIgnoreCase(f.getAirline().getName()))
                .mapToDouble(flight -> {
                    LocalDateTime departure = flight.getDeparture().getScheduled();
                    LocalDateTime arrival = flight.getArrival().getScheduled();
                    return Duration.between(departure, arrival).toHours();
                }).sum();
    }


    public static double getAverageFlightTimeForAirline(List<FlightDTO> flightList, String airlineName) {
        return flightList.stream()
                .filter(f->f.getAirline() !=null && airlineName.equalsIgnoreCase(f.getAirline().getName()))
                .mapToDouble(flight -> {
                    LocalDateTime departure = flight.getDeparture().getScheduled();
                    LocalDateTime arrival = flight.getArrival().getScheduled();
                    return Duration.between(departure, arrival).toMinutes() / 60.0;
                })
                .average().orElse(0.0);
    }

    public static List<FlightDTO> flightsBetweenAirports(List<FlightDTO> flightList, String airport1, String airport2) {
        return flightList.stream()
                .filter(flight -> flight.getDeparture().getAirport() != null && flight.getArrival().getAirport() != null)
                .filter(flight -> flight.getDeparture().getAirport().equalsIgnoreCase(airport1)
                        && flight.getArrival().getAirport().equalsIgnoreCase(airport2))
                .toList();
    }

    public static List<FlightInfoDTO> getFlightsBeforeSpecificTime(
            List<FlightDTO> flightList, LocalTime cutoffTime) {

        return flightList.stream()
                .map(flight -> {
                    LocalDateTime departure = flight.getDeparture().getScheduled();
                    LocalDateTime arrival = flight.getArrival().getScheduled();
                    Duration duration = Duration.between(departure, arrival);

                    return FlightInfoDTO.builder()
                            .name(flight.getFlight().getNumber())
                            .iata(flight.getFlight().getIata())
                            .airline(flight.getAirline().getName())
                            .duration(duration)
                            .departure(departure)
                            .arrival(arrival)
                            .origin(flight.getDeparture().getAirport())
                            .destination(flight.getArrival().getAirport())
                            .build();
                })
                // keep only flights that depart before cutoffTime
                .filter(f -> f.getDeparture().toLocalTime().isBefore(cutoffTime))
                .toList();
    }

    public static double calcAverageFlightTime(List<FlightDTO> flightList) {
        return flightList.stream()
                .filter( flight -> flight.getDeparture() != null)
                .mapToDouble(flight -> {
                    LocalTime departure = LocalTime.from(flight.getDeparture().getScheduled());
                    LocalTime arrival = LocalTime.from(flight.getArrival().getScheduled());
                    return Duration.between(departure,arrival).toMinutes() / 60.0;
                })
                .average().orElse(0.0);
    }

}
