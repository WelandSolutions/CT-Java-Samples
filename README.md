# Compact Talk Java Samples

## Overview

Samples created to help understanding the Compact Talk Web Service interface.

## spring-boot-web-service-client

The sample provides a Web Service client created as a Maven project using Spring Boot and Java 11. The client sends an order with hard coded values to 
elevator Sim_1. When order is in opening and acknowledged on elevator panel an external acknowledge is sent.

### Run Project
1. Start Compact Talk to expose the Web Service interface
2. Run the spring-boot-web-service-client project with: mvn spring-boot:run

You will get two warnings and an error since the interface don't support Last-Modified headers. This will not effect the code generation but you have to look out for changes manually.

[WARNING] The URI [http://localhost:20012/CommandConnection?singleWsdl] seems to represent an absolute HTTP or HTTPS URL. Getting the last modification timestamp is only possible if the URL is accessible and if the server returns the [Last-Modified] header correctly. This method is not reliable and is likely to fail. In this case the last modification timestamp will be assumed to be unknown.
[ERROR] Could not retrieve the last modification timestamp for the URI [http://localhost:20012/CommandConnection?singleWsdl] from the HTTP URL connection. The [Last-Modified] header was probably not set correctly.
[WARNING] Last modification of the URI [http://localhost:20012/CommandConnection?singleWsdl] is not known.


