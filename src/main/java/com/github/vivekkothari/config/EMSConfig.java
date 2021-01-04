package com.github.vivekkothari.config;

/**
 * Config for the Elevator Management System (EMS) application.
 */
public class EMSConfig {

  private final int numberOfLifts;
  private final int numberOfFloors;
  private final EMSRules emsRules;

  public EMSConfig(int numberOfLifts, int numberOfFloors, EMSRules emsRules) {
    this.numberOfLifts = numberOfLifts;
    this.numberOfFloors = numberOfFloors;
    this.emsRules = emsRules;
  }

  public int getNumberOfLifts() {
    return numberOfLifts;
  }

  public int getNumberOfFloors() {
    return numberOfFloors;
  }

  public EMSRules getEmsRules() {
    return emsRules;
  }

}
