# Assignment 3: Bridge & Adapter Pattern Implementation

## 📌 Project Overview
This project demonstrates the combined application of the **Bridge Pattern** and **Adapter Pattern** to build an **Educational Platform Integration Dashboard**. The system aggregates and presents student assignment and assessment data from multiple learning platforms (e.g., *Kundelik.kz*, *BilimLand*, and *AITU LMS*).

By utilizing the **Bridge Pattern**, the system decouples the visualization formats (Abstraction) from the platform data providers (Implementor). Furthermore, the **Adapter Pattern** is integrated to seamlessly connect an incompatible, legacy/external API client (`AituLmsApiClient`) into the standard `PlatformImplementor` interface without modifying the legacy source code.

---

## 🏗 System Architecture & Design Patterns

### 1. Bridge Pattern
The **Bridge Pattern** separates two independent dimensions of variation:
* **Abstraction Axis (View Representation):**
  * **Abstraction Interface / Abstract Class:** `StudentTaskView`
  * **Refined Abstractions:**
    * `QuickSummaryView`: Displays concise progress summaries and high-level grades.
    * `DetailedCodeReviewView`: Displays extended code reviews, submission details, and line-by-line feedback.
* **Implementor Axis (Data Providers):**
  * **Implementor Interface:** `PlatformImplementor`
  * **Concrete Implementors:**
    * `KundelikKzApiClient`: Native provider implementing standard interface.
    * `BilimLandAPIClient`: Native provider implementing standard interface.
    * `AituLmsApiAdapter`: Adapter wrapping an incompatible API client.

### 2. Adapter Pattern
The `AituLmsApiClient` presents a **genuinely incompatible interface** compared to `PlatformImplementor`:

| Feature | Standard `PlatformImplementor` | Incompatible `AituLmsApiClient` |
| :--- | :--- | :--- |
| **Method Name** | `fetchTaskDetails(String studentId, String taskId)` | `retrieveAssignmentPayload(long studentUid, String taskCode)` |
| **Parameter Types** | `String` student ID | `long` student UID (requires parsing) |
| **Failure Mechanism** | Throws standard runtime exceptions | Returns `null` on missing data / throws `AituLmsApiException` |
| **Response Format** | Standardized domain object | Raw `AituTaskPayload` |

#### Exception Translation & Encapsulation
The `AituLmsApiAdapter` fully encapsulates all legacy anomalies:
1. Translates `AituLmsApiException` and `null` responses into a unified domain exception: `PlatformSyncException`.
2. Converts data types (e.g., `String` to `long`) and maps custom payload models to standardized `TaskData`.
3. Ensures **zero leak** of `AituLmsApiClient`-specific models or exceptions to the abstraction layer.

---

## ⚡ Required Complexity Module
* Chosen Complexity Module: **Dynamic Implementor Selection**

The system employs `PlatformFactory` / `PlatformCreator` to dynamically instantiate and select the appropriate `PlatformImplementor` (including the adapted `AituLmsApiAdapter`) at **runtime** based on incoming client configurations or user parameters. The client program never hardcodes concrete implementation classes.

---

## 🎯 Open/Closed Principle (OCP) Compliance

* **Adding a New Abstraction:** Create a new subclass of `StudentTaskView` (e.g., `ExportablePdfView`). No existing `PlatformImplementor` or client code needs to change.
* **Adding a New Implementor:** Implement `PlatformImplementor` directly, or create a new Adapter for another third-party API. Existing views (`QuickSummaryView`, `DetailedCodeReviewView`) remain completely untouched.

---

## 📁 Repository Structure

```
.
├── pom.xml
├── README.md
├── architecture-diagram.png (UML Diagram)
└── src
    ├── main
    │   └── java
    │       └── org
    │           └── aitu
    │               ├── Main.java                      # Client Entry Point
    │               ├── StudentTaskView.java           # Abstraction
    │               ├── QuickSummaryView.java          # Refined Abstraction 1
    │               ├── DetailedCodeReviewView.java    # Refined Abstraction 2
    │               ├── PlatformImplementor.java       # Implementor Interface
    │               ├── KundelikKzApiClient.java       # Concrete Implementor 1
    │               ├── BilimLandAPIClient.java        # Concrete Implementor 2
    │               ├── AituLmsApiClient.java          # Incompatible Class (Adaptee)
    │               ├── AituLmsApiAdapter.java         # Adapter
    │               ├── PlatformSyncException.java     # Unified Domain Exception
    │               ├── PlatformFactory.java           # Dynamic Selection Factory
    │               └── PlatformCreator.java           # Factory Helpers
    └── test
        └── java
            └── org
                └── aitu
                    └── ProjectArchitectureTest.java # JUnit 5 Unit & Architectural Tests
```

---

## 🧪 Testing & Verification

The project includes **JUnit 5** unit tests (`ProjectArchitectureTest`) that utilize mocking (`Mockito`) to verify:
1. **Normal Delegation:** Successful data retrieval across multiple Refined Abstractions (`QuickSummaryView`, `DetailedCodeReviewView`).
2. **Adapter Failure Translation:** Verification that `AituLmsApiException` and invalid inputs are caught by `AituLmsApiAdapter` and wrapped into a `PlatformSyncException`.
3. **Decoupling Verification:** Verification that views only depend on `PlatformImplementor`.

### Running Tests
Execute the following standard Maven command:
```bash
mvn clean test
```

---

## 🛠 Prerequisites & Build Instructions

* **JDK:** Java 17 or higher
* **Build Tool:** Apache Maven 3.6+

### Build Project
```bash
mvn clean package
```

### Run Application
```bash
mvn exec:java -Dexec.mainClass="org.aitu.Main"
```