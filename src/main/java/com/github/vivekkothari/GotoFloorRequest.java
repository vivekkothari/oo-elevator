package com.github.vivekkothari;

public class GotoFloorRequest {

  private final int from;
  private final int to;

  public GotoFloorRequest(String rawInput) {
    String[] split = rawInput.trim().split(" ");
    assert split.length == 2;

    var from = Integer.parseInt(split[0]);
    var to = Integer.parseInt(split[1]);

    assert from >= 0;
    assert to >= 0;
    assert from != to;

    this.from = from;
    this.to = to;
  }

  public int getFrom() {
    return from;
  }

  public int getTo() {
    return to;
  }

  public boolean isUpRequest() {
    return from < to;
  }

  public boolean isDowRequest() {
    return !isUpRequest();
  }

  @Override
  public String toString() {
    return "GotoFloorRequest{" +
        "from=" + from +
        ", to=" + to +
        '}';
  }
}
