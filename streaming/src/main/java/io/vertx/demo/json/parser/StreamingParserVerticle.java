package io.vertx.demo.json.parser;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.parsetools.JsonParser;
import io.vertx.demo.json.model.DataPoint;
import io.vertx.demo.json.model.Statistics;
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
    router.post("/data").consumes("application/json").produces("application/json").handler(this::computeStats);

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
        accumulator.total += dataPoint.getValue();
        accumulator.count++;
        if (accumulator.min == null || accumulator.min.getValue() > dataPoint.getValue()) {
          accumulator.min = dataPoint;
        }
        if (accumulator.max == null || accumulator.max.getValue() < dataPoint.getValue()) {
          accumulator.max = dataPoint;
        }
      }
    });
    request.handler(parser);

    request.endHandler(v -> {
      parser.end();
      routingContext.response().end(Json.encodeToBuffer(accumulator.toStatistics()));
    });
  }

  private static class Accumulator {
    double total;
    int count;
    DataPoint min;
    DataPoint max;

    Statistics toStatistics() {
      return new Statistics(total / count, min, max);
    }
  }
}
