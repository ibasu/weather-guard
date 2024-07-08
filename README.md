<span><img src="vanguard.png" height="60"></span>

## Prerequisites
- JDK 17+ installed with JAVA_HOME configures
- Gradle configured

## Supported technologies

Languages:

* Java

Framework/Module:
* Spring Boot
* Spring Framework
* Spring WebFlux - Reactive

## Getting started

### Problem Statement
### Build a Weather Restful APIs for a given city in a country
Develop SpringBoot application and test a HTTP REST API in that fronts the OpenWeatherMap service: 
OpenWeatherMap name service guide: http://openweathermap.org/current#name . (Example: http://samples.openweathermap.org/data/2.5/weather?q=London,uk)
The service includes:
1. Enforce API Key scheme. An API Key is rate limited to 5 weather reports an hour. 
2. A URL that accepts both a city name and country name.  
3. The API will reject requests with invalid input or missing API Keys.
4. The API stores the data from openweathermap.org into H2 DB.
5. The API will query the data from H2
6. The response from openweather.org has been used to generate response classes using json2schema

#### Field requirements
- country: REQUIRED
- city: REQUIRED
- X-VANGUARD-API-KEY: REQUIRED - The token or API KEY to be used

### Solution

### Project Name : Weather Guard

##### This is a simple gradle java spring boot application which exposes 1 RESTFul API endpoint
The **Weather Guard** microservice is part of the many services provided by the organisation. 
The Weather Guard microservice, is responsible for 
maintaining:

1. An API to query the weather details for a given city in a country

Following is the endpoints exposed by this microservice:
- GET vanguard/api/v1/weather?country={3DigitCountryCode}}&city={cityName}} : Retrieves the weather details for a given city in a given country

##### Design Considerations
- A java based spring boot RESTful application which accepts HTTP requests in JSON format ONLY
- The program returns the custom result object which contains the output and errors(if any)
- The application uses in memory H2 database as the data store
- DB migration is being done using Flyway
- The clients to this API must pass an authorisation header **X-VANGUARD-API-KEY**
- 5 sample values have been mentioned in the application.yml under the property external.authhub.tokens. 
- The sample values configured for the default profile are: sampletoken1, sampletoken2, sampletoken3, sampletoken4, sampletoken5
- The microservice also has an API KEY to invoke the external openweather.org which has been also mentioned in the application.yml in the property **external.openweather.apiKey**
- The microservice has also implemented **Rate Limiter** as per the SLA to restrict 5 requests per hour(configurable)
- The configuration can be adjusted in the application.yml
- The **Rate Limiter** is AOP annotation based and has been implemented in the service layer and not in web layer as only valid tokens should be considered
- Added **jasypt** encryption. Please refer to : https://github.com/ulisesbocchio/jasypt-spring-boot?tab=readme-ov-file
```
Encrypt text, use the following command:
./gradlew encryptProperties --password e.dfjksdifj+dj
```


##### Validation
- The client's API token **X-VANGUARD-API-KEY** is mandatory
- The client's API token **X-VANGUARD-API-KEY** should not be null and must match with the sample ones mentioned in the application.yml

### Build the spring boot application

```
./gradlew 
```
### Generate Test Reports

```
./gradlew jacocoTestReport
```

### Start the gradle Java application

```groovy
./gradlew bootRun # Starts the server at port 8080
```
#### Or
```bash
java -jar build/libs/weather-guard-0.0.1-SNAPSHOT.jar
```

### Swagger
Swagger can be found in the below url:

```
http://localhost:8080/vanguard/swagger-ui.html
```

##### Fetch Weather Details by City and Country Code
```bash
curl --location 'http://localhost:8080/vanguard/api/v1/weather?country=IND&city=Kolkata' \
--header 'X-VANGUARD-API-KEY: sampletoken1'
```
- Response
```json
{
  "description": "haze"
}
```


