package com.github.vivekkothari;

import com.github.vivekkothari.Elevator.ElevatorState;
import com.github.vivekkothari.command.CallElevatorCommand;
import com.github.vivekkothari.command.CloseElevatorCommand;
import com.github.vivekkothari.command.ElevatorCommand;
import com.github.vivekkothari.command.MoveElevatorCommand;
import com.github.vivekkothari.config.EMSConfig;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElevatorLobby {

  private static final Logger log = LoggerFactory.getLogger(ElevatorLobby.class);
  private int timeline;
  public static ElevatorLobby INSTANCE = null;

  private final AtomicBoolean canExit;
  private final List<Elevator> elevators;

  private ElevatorLobby(List<Elevator> elevators, AtomicBoolean canExit) {
    this.elevators = elevators;
    this.canExit = canExit;
  }

  public static ElevatorLobby initializeLobby(EMSConfig config, AtomicBoolean canExit) {
    if (INSTANCE == null) {
      var elevators = IntStream.range(0, config.getNumberOfLifts())
          .mapToObj(i -> Elevator
              .from(i + 1, config.getNumberOfFloors(), config.getEmsRules().getStartFloor()))
          .collect(Collectors.toList());
      INSTANCE = new ElevatorLobby(elevators, canExit);
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
   * Selects the elevator given the request. n = no of elevators f = floors f - 1
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

  public void simulateRun() {
    incrementTimeline();
    do {
      elevators.parallelStream().forEach(elevator -> determineCommand(elevator).execute());
    } while (!canExit.get());

    elevators.forEach(elevator -> log.info(
        String.format("LIFT %d: %d SECONDS%n", elevator.getLiftId(), elevator.getJourneyTime())));
  }

  public void serveRequest(GotoFloorRequest request) {
    var elevator = selectElevator(request);
    if (elevator.isEmpty()) {
      log.warn("No elevator found for request: " + request);
      return;
    }
    log.info("Elevator selected for request: {} is {}", request, elevator.get().getLiftId());
    new CallElevatorCommand(elevator.get(), request).execute();
  }

  public void incrementTimeline() {
    timeline++;
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public int getTimeline() {
    return timeline;
  }

}
