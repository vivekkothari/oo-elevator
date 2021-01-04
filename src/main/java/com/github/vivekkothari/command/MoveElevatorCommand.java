package com.github.vivekkothari.command;

import com.github.vivekkothari.Elevator;

public class MoveElevatorCommand extends ElevatorCommand {

  public MoveElevatorCommand(Elevator elevator) {
    super(elevator);
  }

  @Override
  public void execute() {
    switch (elevator.getState()) {
      case GOING_UP -> {
        if (elevator.nextDestination().isPresent()) {
          int destination = elevator.nextDestination().get();
          if (destination == elevator.getFloor() + 1) {
            elevator.openAndMoveUp();
            elevator.close();
            elevator.pickupComplete();
            return;
          } else if (destination < elevator.getFloor()) {
            elevator.startMovingDown();
            return;
          }
        }
        elevator.move(true);
      }
      case GOING_DOWN -> {
        if (elevator.nextDestination().isPresent()) {
          int destination = elevator.nextDestination().get();
          if (destination == elevator.getFloor() - 1) {
            elevator.openAndMoveDown();
            elevator.close();
            elevator.pickupComplete();
            return;
          } else if (destination > elevator.getFloor()) {
            elevator.startMovingUp();
            return;
          }
        }
        elevator.move(false);
      }
    }
  }
}
