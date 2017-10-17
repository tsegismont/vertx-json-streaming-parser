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
package io.vertx.benchmark;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.parsetools.JsonParser;
import io.vertx.demo.json.model.DataPoint;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static io.vertx.core.parsetools.JsonEventType.*;

/**
 * @author Thomas Segismont
 */
@State(Scope.Thread)
public class JsonParserBenchmark extends BenchmarkBase {

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public static void consume(final DataPoint dataPoint) {
  }

  private Buffer small;
  private Buffer large;

  @Setup
  public void setup() {
    ClassLoader classLoader = getClass().getClassLoader();
    small = loadJsonAsBuffer(classLoader.getResource("small.json"));
    large = loadJsonAsBuffer(classLoader.getResource("large.json"));
  }

  private Buffer loadJsonAsBuffer(URL url) {
    try {
      return new JsonArray(Json.mapper.readValue(url, List.class)).toBuffer();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Benchmark
  public void streamingSmall() throws Exception {
    streaming(small);
  }

  @Benchmark
  public void streamingLarge() throws Exception {
    streaming(large);
  }

  private void streaming(Buffer buffer) {
    JsonParser parser = JsonParser.newParser();
    parser.objectValueMode();
    parser.handler(event -> {
      if (event.type() == VALUE) {
        DataPoint dataPoint = event.mapTo(DataPoint.class);
        consume(dataPoint);
      }
    });
    parser.handle(buffer);
    parser.end();
  }

  @Benchmark
  public void classicSmall() throws Exception {
    classic(small);
  }

  @Benchmark
  public void classicLarge() throws Exception {
    classic(large);
  }

  private void classic(Buffer buffer) {
    List<DataPoint> dataPoints = Json.decodeValue(buffer, new TypeReference<List<DataPoint>>() {});
    dataPoints.forEach(JsonParserBenchmark::consume);
  }
}
