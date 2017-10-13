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

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ParserSimulation extends Simulation {

  // --------------------------- Options

  val url = System.getProperty("url", "http://localhost:8080")

  val clients = Integer.getInteger("clients", 10)
  val ramp = java.lang.Long.getLong("ramp", 1L)
  val loops = Integer.getInteger("loops", 10).toInt
  val interval = Integer.getInteger("interval", 1)

  val jsonBody = System.getProperty("jsonBody")

  // ---------------------------

  val httpProtocol = http
    .baseURL(url)
    .contentTypeHeader("application/json;charset=utf-8")

  val simulation = repeat(loops, "n") {
    exec(http("Report ${n}")
      .post("/data")
      .body(RawFileBody(jsonBody)).asJSON
    ).pause(interval)
  }

  val scn = scenario("ParserSimulation").exec(simulation)
  setUp(scn.inject(rampUsers(clients) over (ramp seconds))).protocols(httpProtocol)
}

