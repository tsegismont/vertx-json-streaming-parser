/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

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
