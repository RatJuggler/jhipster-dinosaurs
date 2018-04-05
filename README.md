# jhipster-dinosaurs

This is just a sandbox to play around with a simple JHipter generated monolith application and deploy it to various cloud providers and containers.

*The original generated README.md has been renamed as [jhipster.md](jhipster.md).*

## Generation

The application was generated using the following commands:

    md jhipster-dinosaurs
    cd jhipster-dinosaurs
    jhipster
    jhipster import-jdl jhipster-dinosaurs.jh

With these responses to the JHipster generation questions:

    ? Which *type* of application would you like to create? Monolithic application (recommended for simple projects)
    ? What is the base name of your application? dinosaurs
    ? What is your default Java package name? com.rj.dinosaurs
    ? Do you want to use the JHipster Registry to configure, monitor and scale your application? No
    ? Which *type* of authentication would you like to use? JWT authentication (stateless, with a token)
    ? Which *type* of database would you like to use? SQL (H2, MySQL, MariaDB, PostgreSQL, Oracle, MSSQL)
    ? Which *production* database would you like to use? MySQL
    ? Which *development* database would you like to use? H2 with disk-based persistence
    ? Do you want to use the Spring cache abstraction? Yes, with the Ehcache implementation (local cache, for a single node)
    ? Do you want to use Hibernate 2nd level cache? Yes
    ? Would you like to use Maven or Gradle for building the backend? Maven
    ? Which other technologies would you like to use?
    ? Which *Framework* would you like to use for the client? Angular 5
    ? Would you like to enable *SASS* support using the LibSass stylesheet preprocessor? No
    ? Would you like to enable internationalization support? No
    ? Besides JUnit and Karma, which testing frameworks would you like to use? Gatling, Cucumber, Protractor
    ? Would you like to install other generators from the JHipster Marketplace? No

The source for the dinosaurs jdl can be found in the [RatJuggler/jhipster-jdl](https://github.com/RatJuggler/jhipster-jdl) repository.

## Heroku

The Heroku deployment can be found at: [https://heroku.dinosaurs.digital](https://heroku.dinosaurs.digital)

The deployment was created and deployed using the following commands:

    jhipster heroku
    mvn package -Pprod -DskipTests
    heroku war:deploy target/dinosaurs.war --app jhipster-dinosaurs

## Pivotal (Cloud Foundry)

The Cloud Foundry deployment can be found at: [http://pivotal.dinosaurs.digital](http://pivotal.dinosaurs.digital)

The deployment was created and deployed using the following commands:

    jhipster cloudfoundry  (Database: cleardb, Plan: spark)
    mvn package -Pprod-DskipTests
    cf push -f ./deploy/cloudfoundry/manifest.yml -t 120 -p target/dinosaurs.war

## Docker

Create a docker image and push to docker hub then run up a local instance:

    mvn dockerfile:build
    mvn dockerfile:push
    docker-compose -f src/main/docker/app.yml up -d

## Kubernetes (minikube)

    md dinosaurs
    cd dinosaurs
    jhipster kubernetes
    kubectl apply -f ./dinosaurs

## Google Cloud

    - Must view Kubernetes in console first
    gcloud container clusters create jhipster-dinosaurs --machine-type=n1-standard-1 --scopes cloud-platform
    - This generates a kubectl context
    gcloud container clusters get-credentials jhipster-dinosaurs

