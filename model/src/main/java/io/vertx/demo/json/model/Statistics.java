package io.vertx.demo.json.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Thomas Segismont
 */
public class Statistics {

  private final double mean;
  private final DataPoint min;
  private final DataPoint max;

  public Statistics(double mean, DataPoint min, DataPoint max) {
    this.mean = mean;
    this.min = min;
    this.max = max;
  }

  @JsonProperty
  public double getMean() {
    return mean;
  }

  @JsonProperty
  public DataPoint getMin() {
    return min;
  }

  @JsonProperty
  public DataPoint getMax() {
    return max;
  }
}
