package com.github.vivekkothari;

import com.github.vivekkothari.Elevator.ElevatorState;
import com.github.vivekkothari.command.CallElevatorCommand;
import com.github.vivekkothari.command.CloseElevatorCommand;
import com.github.vivekkothari.command.ElevatorCommand;
import com.github.vivekkothari.command.MoveElevatorCommand;
import com.github.vivekkothari.config.EMSConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ElevatorLobby {

  private int timeline = -1;
  private Map<Integer, List<GotoFloorRequest>> requests = new HashMap<>();
  public static ElevatorLobby INSTANCE = null;

  private final List<Elevator> elevators;

  private ElevatorLobby(List<Elevator> elevators) {
    this.elevators = elevators;
  }

  public static ElevatorLobby initializeLobby(EMSConfig config) {
    if (INSTANCE == null) {
      var elevators = IntStream.range(0, config.getNumberOfLifts())
          .mapToObj(i -> Elevator
              .from(i + 1, config.getNumberOfFloors(), config.getEmsRules().getStartFloor()))
          .collect(Collectors.toList());
      INSTANCE = new ElevatorLobby(elevators);
    }
    return INSTANCE;
  }

  public boolean areAllLiftsStationary() {
    return elevators.stream().noneMatch(Elevator::isMoving);
  }

  public Optional<Elevator> findGoingUp(int fromFloor) {
    return elevators.stream().filter(
        elevator -> !elevator.isStationary() && elevator.getState() == ElevatorState.GOING_UP)
        .filter(elevator -> elevator.getFloor() < fromFloor)
        .filter(elevator -> elevator.nextDestination().isEmpty()
            || elevator.nextDestination().get() > fromFloor)
        .findAny();
  }

  public Optional<Elevator> findGoingDown(int fromFloor) {
    return elevators.stream().filter(
        elevator -> !elevator.isStationary() && elevator.getState() == ElevatorState.GOING_DOWN)
        .filter(elevator -> elevator.getFloor() > fromFloor)
        .filter(elevator -> elevator.nextDestination().isEmpty()
            || elevator.nextDestination().get() < fromFloor)
        .findAny();
  }

  public Optional<Elevator> findStationary() {
    return elevators.stream().filter(Elevator::isStationary).findAny();
  }

  /**
   * Selects the elevator given the request.
   *
   * @param request
   * @return
   */
  public Optional<Elevator> selectElevator(GotoFloorRequest request) {
    if (request.isUpRequest()) {
      return findGoingUp(request.getFrom()).or(this::findStationary);
    } else if (request.isDowRequest()) {
      return findGoingDown(request.getFrom()).or(this::findStationary);
    }
    return Optional.empty();
  }

  private ElevatorCommand determineCommand(Elevator elevator) {
    if (elevator.isOpen()) {
      return new CloseElevatorCommand(elevator);
    }
    return new MoveElevatorCommand(elevator);
  }

  public void simulateRun(Map<Integer, List<GotoFloorRequest>> incomingRequests) {
    requests.putAll(incomingRequests);
    incrementTimeline();
    do {
      elevators.forEach(elevator -> determineCommand(elevator).execute());
    } while (!areAllLiftsStationary());

    System.out.println();
    elevators.forEach(elevator -> System.out
        .printf("LIFT %d: %d SECONDS%n", elevator.getLiftId(), elevator.getJourneyTime()));
  }

  public void incrementTimeline() {
    timeline++;
    if (requests.containsKey(timeline)) {
      requests.get(timeline)
          .forEach(request -> {
            var elevator = selectElevator(request).orElseThrow(
                () -> new IllegalStateException("No elevator found for request: " + request));
            System.out.printf("Elevator %s found for request: %s", elevator.getLiftId(), request);
            new CallElevatorCommand(elevator, request).execute();
          });
    }
  }

  public int getTimeline() {
    return timeline;
  }

}
