# Rewards Program API

A Spring Boot REST API that calculates customer reward points based on transactions from the last 3 months.

## Features

* Calculate reward points per customer
* Monthly reward aggregation
* Total reward points calculation
* RESTful API design
* Global exception handling
* H2 in-memory database
* Unit, slice, and integration tests for all layers

## Tech Stack

* Java 17
* Spring Boot 3.2.5
* Spring Data JPA
* H2 Database
* JUnit 5
* Mockito

## Reward Calculation Logic

* 2 points for every dollar spent above $100
* 1 point for every dollar spent between $51 and $100
* 0 points for $50 and below

|Amount|Points|
|-|-|
|$25|0|
|$75|25|
|$100|50|
|$120|90|
|$200|250|

## Sample Data

File: `src/main/resources/data.sql`

```sql
INSERT INTO transactions VALUES
(1,  101, 'Seshadri',    120.00, DATEADD('MONTH', -2, CURRENT_DATE)),
(2,  101, 'Seshadri',     75.00, DATEADD('MONTH', -1, CURRENT_DATE)),
(3,  101, 'Seshadri',    200.00, CURRENT_DATE),
(4,  102, 'Raju',        130.00, DATEADD('MONTH', -2, CURRENT_DATE)),
(5,  102, 'Raju',         90.00, DATEADD('MONTH', -1, CURRENT_DATE)),
(6,  102, 'Raju',        180.00, CURRENT_DATE),
(7,  103, 'Kiran',        45.00, DATEADD('MONTH', -2, CURRENT_DATE)),
(8,  103, 'Kiran',        60.00, DATEADD('MONTH', -1, CURRENT_DATE)),
(9,  103, 'Kiran',       140.00, CURRENT_DATE),
(10, 104, 'Anil',         49.00, CURRENT_DATE),
(11, 104, 'Anil',         51.00, CURRENT_DATE),
(12, 104, 'Anil',         99.00, CURRENT_DATE),
(13, 104, 'Anil',        101.00, CURRENT_DATE),
(14, 105, 'Mahesh',      250.00, CURRENT_DATE),
(15, 105, 'Mahesh',      300.00, DATEADD('MONTH', -1, CURRENT_DATE)),
(16, 106, 'OldCustomer', 500.00, DATEADD('MONTH', -6, CURRENT_DATE));
```

Customer 106 has transactions older than 3 months and will not appear in any response.

## How to Run

```
mvn spring-boot:run
```

## API Endpoints

### Get rewards for a specific customer

```
GET /api/rewards/{customerId}
```

```
http://localhost:8080/api/rewards/101
```

```json
{
  "customerId": 101,
  "customerName": "Seshadri",
  "monthlyRewards": [
    { "month": "MARCH", "points": 90  },
    { "month": "APRIL", "points": 25  },
    { "month": "MAY",   "points": 250 }
  ],
  "totalPoints": 365
}
```

### Get rewards for all active customers

```
GET /api/rewards/allCustomers
```

```
http://localhost:8080/api/rewards/allCustomers
```

```json
[
  {
    "customerId": 101,
    "customerName": "Seshadri",
    "monthlyRewards": [
      { "month": "MARCH", "points": 90  },
      { "month": "APRIL", "points": 25  },
      { "month": "MAY",   "points": 250 }
    ],
    "totalPoints": 365
  },
  {
    "customerId": 102,
    "customerName": "Raju",
    "monthlyRewards": [
      { "month": "MARCH", "points": 60  },
      { "month": "APRIL", "points": 40  },
      { "month": "MAY",   "points": 210 }
    ],
    "totalPoints": 310
  }
]
```

### Error response — customer not found

```
GET http://localhost:8080/api/rewards/9999
```

```json
{
  "timestamp": "2026-05-15T10:30:00.123",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 9999"
}
```

## H2 Console

```
http://localhost:8080/h2-console
```

|Field|Value|
|-|-|
|JDBC URL|`jdbc:h2:mem:rewardsdb`|
|Username|`sa`|
|Password|*(leave blank)*|

## Running Tests

```
mvn test
```

|Test Class|Type|Tests|
|-|-|-|
|`RewardCalculatorTest`|Unit|12|
|`RewardControllerTest`|Unit|4|
|`RewardServiceImplTest`|Unit|8|
|`TransactionRepositoryTest`|Slice|5|
|`RewardIntegrationTest`|Integration|7|
|**Total**||**36**|

## Author

Seshadri Reddy



