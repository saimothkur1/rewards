# Rewards Service


### Controller:
* **Endpoint /createtransaction:**  This endpoint allows users to create transactions for a given customer Id.
* This endpoint accepts customer-id and amount. Based on the parameters, points are calculated and persisted into the database.
* The only validation for this endpoint is for the field "amount" which must be greater than 0.
* **Endpoint /getRewardsByDate:** This endpoint allows users to get total points for a customer after given date.
* Date is an optional parameter, when passed points by Month are calculated after the given date.
* If Date is not passed it is defaulted to search points from **3 Months**.

### Data:
* This application leverages in memory database H2.
* H2 is volatile and vanishes everytime the application is restarted.
* Liquibase is used for creating the database and inserting initial data.
* Initial data is persisted using the file initial_data.xml under src/main/resources/liquibase/changelog.
* Initial data allows users to test the /getRewardsByDate endpoint without inserting any additional data.
* When the application loads **Customer ID: "C100"** will have 4 records in the database. 2 Transactions in June, and one each in July and August.


### Steps to Run the Application:
* Clone the application.
* Java 8 or above and maven 3.6 or above must be installed prior to running the application.
* No other Configurations/VM Args are necessary.