package com.github.vivekkothari;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elevator {

  private static final Logger log = LoggerFactory.getLogger(Elevator.class);
  private final int liftId;
  private final int maxFloor;
  private DoorStatus doorStatus;
  private ElevatorState state;
  private int floor;
  private int journeyTime = 0;
  private final Queue<Integer> upDestinations = new PriorityQueue<>();
  private final Queue<Integer> downDestinations = new PriorityQueue<>(Comparator.reverseOrder());

  private Elevator(int liftId, int maxFloor) {
    this.liftId = liftId;
    this.maxFloor = maxFloor;
    this.state = ElevatorState.STATIONARY;
    this.doorStatus = DoorStatus.CLOSED;
  }

  public void enqueue(GotoFloorRequest request) {
    if (request.getFrom() != floor) {
      if (floor < request.getFrom()) {
        upDestinations.add(request.getFrom());
      } else {
        downDestinations.add(request.getFrom());
      }
    }
    if (floor < request.getTo()) {
      upDestinations.add(request.getTo());
    } else {
      downDestinations.add(request.getTo());
    }
    if (state == ElevatorState.STATIONARY) {
      if (request.getFrom() == floor) {
        //Elevator is at same floor the request came from
        open();
      } else {
        startMoving();
      }
    }
  }

  public void open() {
    doorStatus = DoorStatus.OPEN;
    log.info("LIFT " + liftId + " OPENS");
    log.info(this.toString());
    ElevatorLobby.INSTANCE.incrementTimeline();
    journeyTime++;
  }

  public void openAndMoveUp() {
    goUp();
    doorStatus = DoorStatus.OPEN;
    log.info(this.toString());
    ElevatorLobby.INSTANCE.incrementTimeline();
    journeyTime++;
  }

  public void openAndMoveDown() {
    goDown();
    doorStatus = DoorStatus.OPEN;
    log.info(this.toString());
    ElevatorLobby.INSTANCE.incrementTimeline();
    journeyTime++;
  }

  public void startMovingDown() {
    state = ElevatorState.GOING_DOWN;
  }

  public void startMovingUp() {
    state = ElevatorState.GOING_UP;
  }

  public void close() {
    doorStatus = DoorStatus.CLOSED;
    log.info(this.toString());
    ElevatorLobby.INSTANCE.incrementTimeline();
    journeyTime++;
  }

  public void move(boolean up) {
    if (up) {
      goUp();
    } else {
      goDown();
    }
    log.info(this.toString());
    ElevatorLobby.INSTANCE.incrementTimeline();
    journeyTime++;
  }

  private void goUp() {
    if (floor == maxFloor) {
      throw new IllegalStateException("Max floor reached");
    }
    floor++;
  }

  private void goDown() {
    if (floor == 0) {
      throw new IllegalStateException("Ground floor reached");
    }
    floor--;
  }

  public Optional<Integer> nextDestination() {
    return Optional.ofNullable(upDestinations.peek())
        .or(() -> Optional.ofNullable(downDestinations.peek()));
  }

  public void pickupComplete() {
    if (state == ElevatorState.GOING_UP) {
      upDestinations.poll();
    } else if (state == ElevatorState.GOING_DOWN) {
      downDestinations.poll();
    }
    if (upDestinations.isEmpty() && downDestinations.isEmpty()) {
      halt();
    }
  }

  public static Elevator from(int liftId, int maxFloor, int startFloor) {
    var elevator = new Elevator(liftId, maxFloor);
    elevator.floor = startFloor;
    return elevator;
  }

  public boolean isOpen() {
    return doorStatus == DoorStatus.OPEN;
  }

  public void startMoving() {
    nextDestination().ifPresent(destination -> {
      if (destination > floor) {
        state = ElevatorState.GOING_UP;
      } else if (destination < floor) {
        state = ElevatorState.GOING_DOWN;
      }
    });
  }

  public boolean isStationary() {
    return state == ElevatorState.STATIONARY && doorStatus == DoorStatus.CLOSED;
  }

  public void halt() {
    this.state = ElevatorState.STATIONARY;
  }

  public int getLiftId() {
    return liftId;
  }

  public int getJourneyTime() {
    return journeyTime;
  }

  public enum DoorStatus {
    OPEN, CLOSED
  }

  public enum ElevatorState {
    STATIONARY, GOING_UP, GOING_DOWN
  }

  public ElevatorState getState() {
    return state;
  }

  public boolean isMoving() {
    return state != ElevatorState.STATIONARY;
  }

  public int getFloor() {
    return floor;
  }

  @Override
  public String toString() {
    return String
        .format("T=%d\nLIFT %d --> %d (%s) (%s)", ElevatorLobby.INSTANCE.getTimeline(), liftId,
            floor, doorStatus, state);
  }

}
