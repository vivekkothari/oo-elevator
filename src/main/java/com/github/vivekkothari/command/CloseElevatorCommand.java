package com.github.vivekkothari.command;

import com.github.vivekkothari.Elevator;

public class CloseElevatorCommand extends ElevatorCommand {

  public CloseElevatorCommand(Elevator elevator) {
    super(elevator);
  }

  @Override
  public void execute() {
    if (elevator.isOpen()) {
      elevator.close();
      if (elevator.nextDestination().isEmpty()) {
        elevator.halt();
      } else {
        elevator.startMoving();
      }
    }
  }
}
