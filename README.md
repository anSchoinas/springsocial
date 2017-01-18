Spring Social Demo
===============================
This sample app is based on the spring social showcase (https://github.com/spring-projects/spring-social-samples) and demonstrates many of the capabilities of the Spring Social project, including:
* Registering in the application
* Connect to Facebook, Twitter, and LinkedIn
* Sign in using Facebook, Twitter, and Linked in using SocialAuthenticationFilter for provider-signin
* View social profile info through the app, such as pictures, posts, etc

Step 1: Register your application
---------------------------------
Before you can run the application, you'll need to obtain application credentials from Facebook, Twitter, and LinkedIn by registering the application with each of the service providers:

 * Facebook: https://developers.facebook.com/apps
 * Twitter: https://apps.twitter.com/
 * LinkedIn: https://www.linkedin.com/secure/developer

Be sure to read each platform's usage policies carefully and understand how they impact your use of Spring Social with those platforms.

Step 2: Edit application.properties
-----------------------------------
Once you have registered the application, you'll need to edit src/main/resources/application.properties, adding the credentials to the appropriate properties.

Step 3: Run the application
---------------------------
To run, simply import the project into your IDE and deploy to a Servlet 2.5 or > container such as Tomcat 6 or 7, or wildfly.
Access the project at http://localhost:8080/spring-social-showcase

Alternatively, you can run the application using Gradle. To make it easier to build the project with Gradle, the Gradle wrapper has been included. The Gradle wrapper makes it possible to run Gradle without having to explicitly install Gradle to your system.

To run the application with Gradle:

```sh
$ gradlew bootRun
```

Or you can build the application with Gradle, then run the resulting WAR file as an executable JAR:

```sh
$ gradlew build
...
$ java -jar build/libs/spring-social-showcase.war
```

When running the application from the command line, you can access it at http://localhost:8080 from your browser.