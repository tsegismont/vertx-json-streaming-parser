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

package io.vertx.demo.json.parser;

import io.vertx.core.json.Json;
import io.vertx.demo.json.model.DataPoint;
import io.vertx.demo.json.model.Statistics;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.parsetools.JsonParser;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;

import static io.vertx.core.parsetools.JsonEventType.*;

/**
 * @author Thomas Segismont
 */
public class StreamingParserRxVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    Router router = Router.router(vertx);
    router.route().handler(ResponseContentTypeHandler.create());
    router.post("/data").consumes("application/json").produces("application/json").handler(this::computeStats);

    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(8080);
  }

  private void computeStats(RoutingContext routingContext) {
    HttpServerRequest request = routingContext.request();

    JsonParser parser = JsonParser.newParser(request.toFlowable());
    parser.objectValueMode();

    parser.toFlowable()
      .filter(jsonEvent -> jsonEvent.type() == VALUE)
      .map(jsonEvent -> jsonEvent.mapTo(DataPoint.class))
      .collect(Accumulator::new, (accumulator, dataPoint) -> {
        accumulator.total += dataPoint.getValue();
        accumulator.count++;
        if (accumulator.min == null || accumulator.min.getValue() > dataPoint.getValue()) {
          accumulator.min = dataPoint;
        }
        if (accumulator.max == null || accumulator.max.getValue() < dataPoint.getValue()) {
          accumulator.max = dataPoint;
        }
      }).map(Accumulator::toStatistics)
      .subscribe(statistics -> routingContext.response().end(Buffer.newInstance(Json.encodeToBuffer(statistics))));
  }

  private static class Accumulator {
    double total;
    int count;
    DataPoint min;
    DataPoint max;

    Statistics toStatistics() {
      return new Statistics((count == 0) ? 0 : (total / count), min, max);
    }
  }
}
