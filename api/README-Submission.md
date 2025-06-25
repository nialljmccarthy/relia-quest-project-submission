## Introduction
Hi, thanks for taking to time to review my submission.
I added in the swagger apis for ease of use http://localhost:8111/swagger-ui/index.html

## Technical Implementation

I added the EmployeeControllerImpl which implements the EmployeeController interface. This controller handles the CRUD operations for Employee entities.
I did make minor cosmetic changes to the EmployeeController interface where I added in the swagger API summary and description annotations to each method for better documentation.

The EmployeeControllerImpl leverages the ServerApiClient to interact with the Mock Server API at http://localhost:8112/api/v1/employee. 
Each method in the controller corresponds to an endpoint defined in the interface, and it uses the ServerApiClient to perform HTTP requests to the mock server.

Each request from the ServerApiClient uses the RetryTemplate to handle responses and retries in case of failures.
In the application.properties config I have set the retry policy to retry up to 5 times with a backoff period of 30 seconds between retries which should be sufficient for the RandomRequestLimitingInterceptor limit in the Mock server.

I have also included unit tests for the EmployeeControllerImpl amd ServerApiClient to ensure that each method behaves as expected. 
The tests cover various basic scenarios, including successful operations and error handling.
Additionally, I have included the TooManyRequestsTests to confirm that we the 429 Too Many Requests error is handled correctly by the RetryTemplate in the ServerApiClient.

I hope that you find my submission satisfactory.