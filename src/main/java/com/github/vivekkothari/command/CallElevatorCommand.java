package com.github.vivekkothari.command;

import com.github.vivekkothari.Elevator;
import com.github.vivekkothari.GotoFloorRequest;

public class CallElevatorCommand extends ElevatorCommand {

  private final GotoFloorRequest request;

  public CallElevatorCommand(Elevator elevator, GotoFloorRequest request) {
    super(elevator);
    this.request = request;
  }

  @Override
  public void execute() {
    elevator.enqueue(request);
  }
}
