/*
 * Copyright (c) 2019 Pivotal Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.alexandreroman.demos.springhealthcheckdemo;

import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testIndex() {
        final String index = restTemplate.getForObject("http://localhost:" + port, String.class);
        assertThat(index).contains("Application status: UP");
        assertThat(index).contains("http://localhost:" + port + "/getdown");
    }

    @Test
    public void testHealthCheck() {
        final HealthStatus health = restTemplate.getForObject("http://localhost:" + port + "/actuator/health", HealthStatus.class);
        assertThat(health.status).isEqualTo("UP");
        restTemplate.getForObject("http://localhost:" + port + "/getdown", String.class);

        final HealthStatus newHealth = restTemplate.getForObject("http://localhost:" + port + "/actuator/health", HealthStatus.class);
        assertThat(newHealth.status).isEqualTo("DOWN");
    }

    @Data
    private static class HealthStatus {
        private String status;
    }
}
