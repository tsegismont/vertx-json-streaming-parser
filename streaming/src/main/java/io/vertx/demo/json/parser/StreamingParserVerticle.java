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

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.parsetools.JsonParser;
import io.vertx.demo.json.model.Accumulator;
import io.vertx.demo.json.model.DataPoint;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;

import static io.vertx.core.parsetools.JsonEventType.*;

/**
 * @author Thomas Segismont
 */
public class StreamingParserVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    Router router = Router.router(vertx);
    router.route().handler(ResponseContentTypeHandler.create());
    router.post("/compute").consumes("application/json").produces("application/json").handler(this::computeStats);

    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(8080);
  }

  private void computeStats(RoutingContext routingContext) {
    HttpServerRequest request = routingContext.request();

    Accumulator accumulator = new Accumulator();

    JsonParser parser = JsonParser.newParser();
    parser.objectValueMode();
    parser.handler(event -> {
      if (event.type() == VALUE) {
        DataPoint dataPoint = event.mapTo(DataPoint.class);
        accumulator.accumulate(dataPoint);
      }
    });
    request.handler(parser);

    request.endHandler(v -> {
      parser.end();
      routingContext.response().end(Json.encodeToBuffer(accumulator.toStatistics()));
    });
  }
}
