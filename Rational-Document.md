# Design Rationale Document

**Project:** Educational Platform Integration Dashboard (Assignment 3)

**Author:** Maksat Ailbekov, Group SE-2534, Astana IT University

---

## 1. Overview & System Goals

The main problem I set out to solve is that teachers and students often have to juggle multiple educational systems simultaneously, like working across different schools. I needed a way to pull all this scattered data into a single, unified dashboard.

The challenge was that the platforms I integrated—*Kundelik.kz*, *BilimLand*, and *AITU LMS*—speak completely different languages. While the first two return clean JSON, the *AITU LMS* client spits out a nested structure where an XML string is buried inside a JSON field. On top of that, users need to view this data in various ways, like a quick summary (`QuickSummaryView`) or an in-depth code review (`DetailedCodeReviewView`). I wanted these viewing formats to evolve independently from how the data is actually fetched.

## 2. Why I Chose Bridge and Adapter

### The Bridge Pattern

I went with the Bridge pattern to avoid a combinatorial class explosion. If I had used standard inheritance, adding just one new view or one new platform would mean creating a bunch of new subclasses (an $N \times M$ growth problem). The Bridge pattern let me split the logic into two completely independent tracks:

* **The Abstraction:** This is my abstract `StudentTaskView` class, which gets refined into specific formats like `QuickSummaryView` and `DetailedCodeReviewView`.


* **The Implementation:** This is handled by the `PlatformImplementor` interface and its concrete data-fetching classes, `BilimLandAPIClient` and `KundelikKzApiClient`.



I connected these two sides through composition—specifically, by dropping a `protected final PlatformImplementor platform` field into the `StudentTaskView` class.

### The Adapter Pattern

The `AituLmsApiClient` was a bit of a headache because its methods (`getData`, `fetchXmlResponse`) and overall behavior just didn't fit the clean `PlatformImplementor` interface I had built.

To fix this without rewriting the legacy code, I used an **Object Adapter**. I created `AituLmsApiAdapter`, which implements my target interface but secretly wraps an instance of the messy client inside it via a `private final AituLmsApiClient client` field. This adapter does all the dirty work of parsing the XML/JSON (`extractJsonField` and `extractXmlTag`) behind the scenes, handing over perfectly formatted strings and numbers to the rest of the system.

While I planned the Bridge from the start to separate the view from the data, the Adapter was a tactical fix to force a stubborn, incompatible class to play by my rules.

## 3. SOLID Principles & Error Handling

### Dynamic Selection & the Open/Closed Principle (OCP)

I wanted the system to be smart enough to pick the right platform on the fly. So, I built a `PlatformFactory` with a `Map<String, PlatformCreator>` registry that looks at the `"platform"` field in the incoming JSON and figures out which implementation to instantiate. This perfectly respects the Open/Closed Principle: if I want to add a new platform later, I just register it using a lambda expression in the `Main` class (using `PlatformFactory.register()`) without touching the factory's internal code.

### Single Responsibility Principle (SRP)

I made sure every layer sticks strictly to its own job. The views (`StudentTaskView`) only care about formatting text for the console, the API clients only care about extracting data, and the factory is strictly in charge of creating objects.

### Error Handling Strategy

I didn't want the UI layers crashing just because a data source threw a weird XML parsing error. If the `AituLmsApiAdapter` messes up while reading XML or converting a number, it catches that generic exception and repackages it into a custom `PlatformSyncException`. This keeps the presentation layer safe and oblivious to API-specific failures.

## 4. Alternatives Considered & Testing

### What I Didn't Do

I originally thought about writing a massive `switch-case` block to check the platform type and hardcoding the parsing logic right into the views. I scrapped that idea because it would completely violate OCP and make the code a nightmare to maintain as more schools are added. The trade-off for my current, cleaner design is a slight bump in the total number of classes and interfaces, but it's well worth it for the long-term flexibility.

### Testing & Verification

To prove the architecture works, I wrote unit tests (`ProjectArchitectureTest`) using JUnit 5 and Mockito. These tests verify that the views properly delegate tasks to the implementors, and they specifically check that the adapter successfully catches errors from the incompatible `AituLmsApiClient` and safely converts them into a `PlatformSyncException`.