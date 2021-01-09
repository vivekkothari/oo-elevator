package com.github.vivekkothari;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElevatorDispatcher {

  private static final Logger log = LoggerFactory.getLogger(ElevatorDispatcher.class);

  private final BlockingQueue<GotoFloorRequest> queue;
  private AtomicBoolean canExit;
  private final ElevatorLobby lobby;

  public ElevatorDispatcher(ElevatorLobby lobby, int queueSize, AtomicBoolean canExit) {
    this.lobby = lobby;
    this.queue = new ArrayBlockingQueue<>(queueSize);
    this.canExit = canExit;
  }

  public void readRequest() {
    try {
      var reader = new BufferedReader(new InputStreamReader(System.in));
      do {
        log.info("Please enter a new destination: ");
        var line = reader.readLine();
        if ("NO".equalsIgnoreCase(line)) {
          canExit.set(true);
          break;
        }
        queue.put(new GotoFloorRequest(line));
        log.info("Added to queue: " + queue);
      } while (true);
    } catch (InterruptedException | IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public void serveRequest() {
    try {
      GotoFloorRequest request;
      //consuming messages until exit message is received
      while ((request = queue.take()).getTo() != -1) {
        log.info("Read from queue: " + queue);
        lobby.serveRequest(request);
      }
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    }
  }

}
