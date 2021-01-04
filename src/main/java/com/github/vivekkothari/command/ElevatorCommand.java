package com.github.vivekkothari.command;

import com.github.vivekkothari.Elevator;

public abstract class ElevatorCommand {

  protected final Elevator elevator;

  protected ElevatorCommand(Elevator elevator) {
    this.elevator = elevator;
  }

  public abstract void execute();

}
