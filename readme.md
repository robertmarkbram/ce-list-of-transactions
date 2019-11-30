# Coding Exercise - list of transactions

This is a coding exercise I submitted for a job application.

## Aims

Show the following:

- Immutable data classes.
- Fluent building of data classes.
- Demonstrate use of lambdas and streams where appropriate.
- Data driven testing via parameterised unit tests.
- Show maven as a build tool.

## What I deliberately left out

Mechanisms I left out because of the small scope of the exercise.

- Logging mechanisms.
- Interfaces to isolate service functions.
- Dependency injection. Services are POJOs that clients will create here.

# The Problem

The goal of the challenge is to implement a system that analyses financial transaction records.

A transaction record describes transferring money from one account to another account. As such, each transaction record will have the following fields:

- `transactionId` – The id of the transaction
- `fromAccountId` – The id of the account to transfer money from
- `toAccountId` – The id of the account to transfer money to
- `createAt` – the date and time the transaction was created (in the format of “DD/MM/YYYY hh:mm:ss”)
- `amount` – The amount that was transferred including dollars and cents
- `transactionType` – The type of the transaction which could be either PAYMENT or REVERSAL.
- `relatedTransaction` – In case of a REVERSAL transaction, this will contain the id of the transaction it is reversing. In case of a PAYMENT transaction this field would be empty.

The system will be initialised with an input file in CSV format containing a list of transaction records.

Once initialised it should be able to print the relative account balance (positive or negative) in a given time frame.

The relative account balance is the sum of funds that were transferred to / from an account in a given time frame, it does not account for funds that were in that account prior to the timeframe.

Another requirement is that, if a transaction has a reversing transaction, this transaction should be omitted from the calculation, even if the reversing transaction is outside the given time frame.

## Example Data

The following data is an example of an input CSV file

```csv
transactionId, fromAccountId, toAccountId, createdAt, amount, transactionType, relatedTransaction
TX10001, ACC334455, ACC778899, 20/10/2018 12:47:55, 25.00, PAYMENT
TX10002, ACC334455, ACC998877, 20/10/2018 17:33:43, 10.50, PAYMENT
TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT
TX10004, ACC334455, ACC998877, 20/10/2018 19:45:00, 10.50, REVERSAL, TX10002
TX10005, ACC334455, ACC778899, 21/10/2018 09:30:00, 7.25, PAYMENT
```

## Assumptions

For the sake of simplicity, it is safe to assume that

- input file and records are all in a valid format
- File will always present transactions by ID order, which implies date order 
  - so no need to sort on date; we can use transactions in order they appear within the CSV.
- A reversal transaction will always occur after the transaction it reverses (by order of ID).
- A reversal transaction will never refer to a related transaction ID that doesn't exist.

# How to build and run

Requires JDK 11 and a recent version of Maven. Check your environment with `mvn -version`:

```bash
$ mvn -version
Apache Maven 3.6.2 (40f52333136460af0dc0d7232c0dc0bcf0d9e117; 2019-08-28T01:06:16+10:00)
Maven home: D:\Rob\myApps\apache-maven-3.6.2
Java version: 11.0.5, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-11.0.5
Default locale: en_US, platform encoding: Cp1252
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
```

Assuming you have JDK 11 and a recent Maven, clone the repository:

```bash
cd /where/to/check/out/project
git clone git@github.com:robertmarkbram/ce-list-of-transactions.git
cd ce-list-of-transactions
```

Build the project, which also runs tests:

```bash
mvn clean package
```

Run the application with:

```bash
java -cp target/ce-list-of-transactions-1.0-SNAPSHOT.jar org.rob.bank.controller.App
```

## Example Input and Output

Sample run is shown below.

Getting only some of the transactions from a given account that includes a reversal.

```
Welcome! Please enter search criteria. Control+c to exit at any time.

accountId: ACC334455
from (dd/MM/yyyy HH:mm:ss): 20/10/2018 12:00:00
to (dd/MM/yyyy HH:mm:ss): 20/10/2018 19:00:00

Relative balance for the period is: -$25.00
Number of transactions included is: 1
```

Getting all of them.

```
Please enter search criteria. Control+c to exit at any time.

accountId: ACC334455
from (dd/MM/yyyy HH:mm:ss): 20/10/2018 12:00:00
to (dd/MM/yyyy HH:mm:ss): 22/10/2018 19:00:00

Relative balance for the period is: -$32.25
Number of transactions included is: 2
```

Searching with a date range that misses all transactions.

```
Please enter search criteria. Control+c to exit at any time.

accountId: ACC334455
from (dd/MM/yyyy HH:mm:ss): 20/10/2000 12:00:00
to (dd/MM/yyyy HH:mm:ss): 22/10/2001 19:00:00

Relative balance for the period is: $0.00
Number of transactions included is: 0

Please enter search criteria. Control+c to exit at any time.

accountId: ACC334455
from (dd/MM/yyyy HH:mm:ss): 20/10/2100 12:00:00
to (dd/MM/yyyy HH:mm:ss): 22/10/2101 19:00:00

Relative balance for the period is: $0.00
Number of transactions included is: 0
```

Searching on a second account that includes a reversal.

```
Please enter search criteria. Control+c to exit at any time.

accountId: ACC998877
from (dd/MM/yyyy HH:mm:ss): 20/10/2018 12:00:00
to (dd/MM/yyyy HH:mm:ss): 22/10/2018 19:00:00

Relative balance for the period is: -$5.00
Number of transactions included is: 1
```

Searching with account ID that doesn't exist and showing bad date entry.

```
Please enter search criteria. Control+c to exit at any time.

accountId: nosuchaccount
from (dd/MM/yyyy HH:mm:ss): 20/10/2018 12:00:00
to (dd/MM/yyyy HH:mm:ss): 22/10/2018 19:00:00

Relative balance for the period is: $0.00
Number of transactions included is: 0

Please enter search criteria. Control+c to exit at any time.

accountId: nosuchaccount
from (dd/MM/yyyy HH:mm:ss): bad date
Invalid date format. Need dd/MM/yyyy HH:mm:ss. Please try again.
from (dd/MM/yyyy HH:mm:ss): 20/10/2018 12:00:00
to (dd/MM/yyyy HH:mm:ss): 22/10/2018 19:00:00

Relative balance for the period is: $0.00
Number of transactions included is: 0

Please enter search criteria. Control+c to exit at any time.

accountId: nosuchaccount
from (dd/MM/yyyy HH:mm:ss): 22/10/2018 19:00:00
to (dd/MM/yyyy HH:mm:ss): 20/10/2018 12:00:00

Invalid date range (to must be before from). Please try again.
```

Searching with account ID that received payments.

```
accountId: ACC778899
from (dd/MM/yyyy HH:mm:ss): 01/10/2018 19:00:00
to (dd/MM/yyyy HH:mm:ss): 01/11/2018 12:00:00

Relative balance for the period is: $37.25
Number of transactions included is: 3
```