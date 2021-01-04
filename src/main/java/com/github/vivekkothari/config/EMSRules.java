package com.github.vivekkothari.config;

public class EMSRules {

  private int startFloor = 0;
  private int unitConsumedPerFloorMove = 1;
  private int unitConsumedForDoorOpen = 1;
  private int unitConsumedForDoorClose = 1;

  public int getStartFloor() {
    return startFloor;
  }

  public void setStartFloor(int startFloor) {
    this.startFloor = startFloor;
  }

  public int getUnitConsumedPerFloorMove() {
    return unitConsumedPerFloorMove;
  }

  public void setUnitConsumedPerFloorMove(int unitConsumedPerFloorMove) {
    this.unitConsumedPerFloorMove = unitConsumedPerFloorMove;
  }

  public int getUnitConsumedForDoorOpen() {
    return unitConsumedForDoorOpen;
  }

  public void setUnitConsumedForDoorOpen(int unitConsumedForDoorOpen) {
    this.unitConsumedForDoorOpen = unitConsumedForDoorOpen;
  }

  public int getUnitConsumedForDoorClose() {
    return unitConsumedForDoorClose;
  }

  public void setUnitConsumedForDoorClose(int unitConsumedForDoorClose) {
    this.unitConsumedForDoorClose = unitConsumedForDoorClose;
  }
}
