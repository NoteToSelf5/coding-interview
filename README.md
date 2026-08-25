# Fishing Records API — Coding Challenge

A small Spring Boot API that stores fishing catch records in a CSV file. You have **30 minutes**
total for the three tasks below.

## Before you start

- **No AI coding assistants** (Copilot, ChatGPT, Claude, or similar) for this session.
- Please **share your screen** for the full 30 minutes so your interviewer can follow along.
- Ask clarifying questions any time — this README plus the interviewer are your only resources;
  there's no need (and no time) to search external docs for anything covered here.

## Build & run

Requires a JDK 11+ install. No local Maven install needed — use the wrapper:

```bash
./mvnw spring-boot:run
```

The API starts on port 8080, backed by the seeded CSV at
`src/main/resources/data/fishing-records-seed.csv`.

This project uses [Lombok](https://projectlombok.org/) (see `FishingRecord`). If your IDE shows
"cannot find symbol" for methods like `getSpecies()` that aren't written explicitly in the
code, install your IDE's Lombok plugin and enable annotation processing — the command-line
build above works either way.

## Run the tests

```bash
./mvnw test
```

Each task below has one example test already in `src/test/java/.../`, showing you the expected
contract. (Additional tests are used for grading after the session — you don't need to write
your own tests, just make the existing one pass and keep the others green.)

## The three tasks (~8 / ~10 / ~12 minutes respectively)

### 1. Add a new endpoint (~8 min)

Add `GET /api/fishing-records/top-by-species`, returning the single largest-weight record for
each distinct species in the data store. See `NewEndpointStory1ExampleTest` for the expected
shape.

### 2. Refactor (~10 min)

`GET /api/fishing-records?location=...` currently works, but its CSV read/write logic is
inlined directly into `FishingRecordController`. Extract it into a separate persistence
class/method(s) so request handling and CSV I/O aren't tangled together. **Behavior must not
change** — `RefactorStory2ExampleTest` (and the grading tests) must still pass exactly as
before.

### 3. Fix a bug (~12 min)

The same location-filter endpoint has a bug: some queries return records from the wrong
location. Find and fix it. `BugfixStory3ExampleTest` shows the endpoint's normal contract; your
fix should not change that behavior, only correct the mismatch.

## If you run out of time on task 2

Tell your interviewer — they can give you a reference version of the refactor so you can still
attempt task 3 independently.
