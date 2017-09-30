package io.vertx.demo.json.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Thomas Segismont
 */
public class DataPoint {

  private final long timestamp;
  private final double value;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public DataPoint(@JsonProperty("ts") long timestamp, @JsonProperty("val") double value) {
    this.timestamp = timestamp;
    this.value = value;
  }

  @JsonProperty("ts")
  public long getTimestamp() {
    return timestamp;
  }

  @JsonProperty("val")
  public double getValue() {
    return value;
  }
}
