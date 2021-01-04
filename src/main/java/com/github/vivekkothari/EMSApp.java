package com.github.vivekkothari;

import com.github.vivekkothari.config.EMSConfig;
import com.github.vivekkothari.config.EMSRules;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class EMSApp {

  public static void main(String[] args) throws IOException {

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
      System.out.println("Welcome to Elevator management system.");
      System.out.print("Please enter number of lifts: ");
      var lifts = Integer.parseInt(reader.readLine());
      System.out.print("Please enter number of floors: ");
      var floors = Integer.parseInt(reader.readLine());

      var config = new EMSConfig(lifts, floors, new EMSRules());
      var lobby = ElevatorLobby.initializeLobby(config);

      var requests = Map.of(
          0, List.of(new GotoFloorRequest(0, 7), new GotoFloorRequest(3, 0)
          ),
          2, List.of(new GotoFloorRequest(4, 6))
      );

      System.out.println();
      lobby.simulateRun(requests);

    }

  }


}
