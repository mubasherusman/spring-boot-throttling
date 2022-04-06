# Spring Boot Throttling
![Java CI with Maven](https://github.com/vicebac/spring-boot-throttling/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)

### Overview

Declarative approach of throttling control over the Spring services.
`@Throttling` annotation helps you to limit the number of service method calls per `java.util.concurrent.TimeUnit`
for a particular user, IP address, HTTP header/cookie value, or using [Spring Expression Language (SpEL)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions).

Please see [example project](https://github.com/nielsbasjes/spring-boot-throttling/tree/master/example). Pull requests are welcome.


### Project history
Initially this was created in 2017 by Nikolay Papakha and was made available via https://github.com/weddini/spring-boot-throttling .
At that time releases were made available by means of committing them to a 'mvn-repo' branch in github which should then be included as a mvn repo in downstream projects.

For unknown reasons the entire original repo is now gone and only forks remain as a consequence any project that followed the original instructions can no longer be built.

Mid 2019 [Michaël COLL](https://github.com/michaelcoll/spring-boot-throttling) made a lot of improvements but this was never been released.

Mid 2020 [Niels Basjes](https://github.com/nielsbasjes/spring-boot-throttling) picked up what Michaël COLL had created and made steps towards maturing the project and actually releasing it on maven central.
In order to do that correctly all packages were moved from `com.weddini` to `nl.basjes.weddini`

### Getting Started

#### Gradle setup
Add the following code to dependencies section of your build.gradle:

```groovy
compile('nl.basjes.weddini.throttling:spring-boot-throttling-starter:0.0.12')
```

#### Maven setup
Add the following code to dependencies section of your pom.xml:

```xml
<dependency>
    <groupId>nl.basjes.weddini.throttling</groupId>
    <artifactId>spring-boot-throttling-starter</artifactId>
    <version>0.0.12</version>
</dependency>
```


### Samples

#### Defaults (Remote IP)
The following throttling configuration allows 1 method calls per SECOND for each unique `HttpServletRequest#getRemoteAddr()`.
This is 'defaults' for `@Throttling` annotation.

**IMPORTANT** : Annotated method with `@Throttling` must be public.

```java
@Throttling
public void serviceMethod() {
}
```
is the same as:

```java
@Throttling(type = ThrottlingType.RemoteAddr, limit = 1, timeUnit = TimeUnit.SECONDS)
public void serviceMethod() {
}
```

#### Spring Expression Language (SpEL)
The following throttling configuration allows 3 method calls per MINUTE for each unique userName in model object passed as parameter, i.e. `model.getUserName()`.

Please refer to official [docs on SpEL](https://docs.spring.io/spring/docs/4.3.12.RELEASE/spring-framework-reference/html/expressions.html).

```java
@Throttling(type = ThrottlingType.SpEL, expression = "#model.userName", limit = 3, timeUnit = TimeUnit.MINUTES)
public void serviceMethod(Model model) {
    log.info("executing service logic for userName = {}", model.getUserName());
}
```

#### Http cookie value
The following throttling configuration allows 24 method calls per DAY for each unique cookie value retrieved from `HttpServletRequest#getCookies()`.

```java
@Throttling(type = ThrottlingType.CookieValue, cookieName = "JSESSIONID", limit = 24, timeUnit = TimeUnit.DAYS)
public void serviceMethod() {
}
```

#### Http header value
The following throttling configuration allows 10 method calls per HOUR for each unique header value retrieved from `HttpServletRequest#getHeader('X-Forwarded-For')`.

```java
@Throttling(type = ThrottlingType.HeaderValue, headerName = "X-Forwarded-For", limit = 10, timeUnit = TimeUnit.HOURS)
public void serviceMethod() {
}
```

#### User Principal Name
The following throttling configuration allows 1 method calls per HOUR for each unique `HttpServletRequest#getUserPrincipal().getName()`.

```java
@Throttling(type = ThrottlingType.PrincipalName, limit = 1, timeUnit = TimeUnit.HOURS)
public void serviceMethod() {
}
```


### Error handling

`ThrottlingException` is thrown when method reaches `@Throttling` configuration limit. Service method won't be executed.

```java
@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS, reason = "Too many requests")
public class ThrottlingException extends RuntimeException {
}
```
![Throttling with http header. Exception-handling.](./assets/throttling-with-header-exception-handling.png)


### License
Spring Boot Throttling is Open Source software released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
