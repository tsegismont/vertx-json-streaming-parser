package io.vertx.demo.json.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.demo.json.model.DataPoint;
import io.vertx.demo.json.model.Statistics;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;

import java.util.List;

/**
 * @author Thomas Segismont
 */
public class ClassicParserVerticle extends AbstractVerticle {

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
    routingContext.request().bodyHandler(body -> {
      double total = 0D;
      DataPoint min = null;
      DataPoint max = null;
      Statistics statistics;
      if (body.length() != 0) {
        List<DataPoint> dataPoints = Json.decodeValue(body, new TypeReference<List<DataPoint>>() {});
        for (DataPoint dataPoint : dataPoints) {
          total += dataPoint.getValue();
          if (min == null || min.getValue() > dataPoint.getValue()) {
            min = dataPoint;
          }
          if (max == null || max.getValue() < dataPoint.getValue()) {
            max = dataPoint;
          }
        }
        statistics = new Statistics(total / dataPoints.size(), min, max);
      } else {
        statistics = new Statistics(0D, null, null);
      }
      routingContext.response().end(Json.encodeToBuffer(statistics));
    });
  }
}
