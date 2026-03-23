package com.citycare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * ============================================================
 * CityCareApplication.java – Application Entry Point
 * ============================================================
 *
 * @SpringBootApplication combines three annotations:
 * @Configuration – marks this as a Spring config class
 * @EnableAutoConfiguration – lets Spring Boot auto-configure
 *                          Hibernate, Security, Web MVC etc.
 * @ComponentScan – scans com.citycare.* for all beans
 *                (controllers, services, repos, etc.)
 *
 * @EnableJpaAuditing – activates automatic timestamps.
 *                    When any entity is saved, Spring auto-fills createdAt.
 *                    When updated, Spring auto-fills updatedAt.
 *                    Requires BaseEntity.java to have @CreatedDate
 *                    / @LastModifiedDate.
 *
 *                    ============================================================
 *                    HOW THIS FILE WORKS:
 *                    1. main() calls SpringApplication.run()
 *                    2. Spring Boot starts embedded Tomcat on port 8080
 *                    3. All @Entity classes are read by Hibernate
 *                    4. Hibernate connects to MySQL and runs:
 *                    CREATE TABLE IF NOT EXISTS users (...)
 *                    CREATE TABLE IF NOT EXISTS emergencies (...)
 *                    ... etc for all 5 tables
 *                    5. All @RestController classes are registered as HTTP
 *                    endpoints
 *                    6. App is ready at http://localhost:8080/api
 *                    ============================================================
 */
@SpringBootApplication
@EnableJpaAuditing
public class CityCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(CityCareApplication.class, args);
    }
}
