package com.github.vivekkothari;

import com.github.vivekkothari.config.EMSConfig;
import com.github.vivekkothari.config.EMSRules;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EMSApp {

  private static final Logger log = LoggerFactory.getLogger(EMSApp.class);

  public static void main(String[] args) throws IOException {

    int floors, lifts;

    AtomicBoolean canExit = new AtomicBoolean(false);

    var reader = new BufferedReader(new InputStreamReader(System.in));
    log.info("Welcome to Elevator management system.");
    log.info("Please enter number of lifts: ");
    lifts = Integer.parseInt(reader.readLine());
    log.info("Please enter number of floors: ");
    floors = Integer.parseInt(reader.readLine());

    var config = new EMSConfig(lifts, floors, new EMSRules());
    var lobby = ElevatorLobby.initializeLobby(config, canExit);
    var dispatcher = new ElevatorDispatcher(lobby, floors, canExit);

    new Thread(dispatcher::readRequest, "producer").start();
    new Thread(dispatcher::serveRequest, "consumer").start();
    new Thread(lobby::simulateRun, "simulator").start();
  }


}
