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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@RestController
@RequiredArgsConstructor
@Slf4j
class IndexController {
    private final AppStatus status;

    @GetMapping(value = "/", produces = MimeTypeUtils.TEXT_PLAIN_VALUE)
    String index() {
        final String url = MvcUriComponentsBuilder.fromMethodName(IndexController.class, "getDown").build().toUriString();
        return "Application status: " + (status.isLive() ? "UP" : "DOWN")
                + "\nHit " + url + " to update application status, and see how the platform reacts to this update.";
    }

    @GetMapping(value = "/getdown", produces = MimeTypeUtils.TEXT_PLAIN_VALUE)
    String getDown() {
        log.info("Updating application status: DOWN");
        status.setLive(false);
        return "Application status set to DOWN";
    }
}

@RestController
@Slf4j
class KillController {
    @GetMapping("/kill")
    void kill() {
        log.info("Killing application process");
        System.exit(0);
    }
}

@Configuration
@RequiredArgsConstructor
class AppConfig {
    private final AppStatus status;

    @Bean
    HealthIndicator healthIndicator() {
        return () -> status.isLive() ? Health.up().build() : Health.down().build();
    }
}

@Data
@Component
class AppStatus {
    private boolean live = true;
}
