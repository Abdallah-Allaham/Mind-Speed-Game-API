# Mind Speed Game API

## Tech

* **Backend Language**: Java 17
* **Framework**: Spring Boot
* **Database**: H2 Database
* **Helper Libraries**:
    * `exp4j`: Used this to calculate expressions as a string. It saved me a lot of time!
    * `Lombok`:It helped me keep my code much cleaner and shorter by handling boilerplate code (like Getters and Setters) automatically.

## How to Run Project

### Prerequisites
* Java Development Kit (JDK) 17.
* Apache Maven 3.5.3

### Steps to Run

1.  **Clone the Repository**:git clone https://github.com/Abdallah-Allaham/Mind-Speed-Game-API.git
2.  cd Mind-Speed-Game-API
3. **Build the Project**: to download all necessary dependencies and compile the project, run the Maven clean install command:
    ```bash
    mvn clean install
    ```
4. **Run the Application**:
   Finally, to start the Spring Boot API application:
    ```bash
    mvn spring-boot:run
    ```   


The API should now be running on port `8080`.
    You can access the H2 Database Console at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:mind_speed_db`,Username: `sa`, Password: leave it empty).

## How to Test the API (with Examples)

### 1. Start a New Game (`POST /game/start`)
`http://localhost:8080/game/start`
* **Type**: `POST`
* **Example Postman Request (JSON Body)**:
    ```json
    {
        "name": "Abdallah",
        "difficulty": 1
    }
    ``` 
* **Example Postman Response (JSON Body)**:
    ```json
    {
        "message": "Hello Abdallah find your submit API URL below",
        "submitUrl": "/game/1/submit",
        "question": "3 - 3",
        "timeStarted": "2025-06-25T16:23:39.5594188"
    }
    ``` 

### 2. Submit an Answer (`POST /game/{gameId}/submit`)

`http://localhost:8080/game/{gameId}/submit`
* **Type**: `POST`
* replace `{gameId}` with the Game ID.
* **Example Postman Request (JSON Body)**:
    ```json
    {
        "answer": 123.45
    }
    ```
* **Example Postman Response (JSON Body)**:
  ```json
   {
      "result": "Good job Abdallah, your answer is correct!",
      "timeTaken": 116.68,
      "nextQuestion": {
          "submitUrl": "/game/1/submit",
          "question": "5 * 0",
          "createdAt": "2025-06-25T16:25:36.439676"
        },
      "currentScore": "1/1"
   }
  ```

### 3. End Game (`GET /game/{gameId}/end`)
`http://localhost:8080/game/{gameId}/end`

* **Type**: `GET`
* replace `{gameId}` with the Game ID.

* **Example Postman Response (JSON Body)**:
  ```json
   {
    "name": "Abdallah",
    "difficulty": 1,
    "currentScore": "1/1",
    "totalTimeSpent": 116.68,
    "bestScoreDetails": {
        "question": "3 - 3",
        "answer": 0.0,
        "timeTaken": 116.68
    },
    "history": [
        {
            "question": "3 - 3",
            "playerAnswer": 0.0,
            "correctAnswer": 0.0,
            "timeTaken": 116.68,
            "isCorrect": true
        },
        {
            "question": "5 * 0",
            "playerAnswer": null,
            "correctAnswer": 0.0,
            "timeTaken": null,
            "isCorrect": null
        }
    ]
}
  ```
