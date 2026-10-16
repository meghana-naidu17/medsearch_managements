# MedSearch Medical Management & Intelligent Search System

A robust, enterprise-grade Java Healthcare Management System featuring classic algorithmic analysis and a dedicated **String Matching Search Engine** applied to comprehensive real-world clinical datasets.

---

## Key Enhancements & Bugs Fixed

### 1. Merged Comprehensive Healthcare Dataset
Merged and integrated realistic multi-table datasets covering:
- **Patients Dataset** (`data/patients.csv`): 25 patient records with complete demographics (ID, Name, Age, Gender, Phone, Password).
- **Doctors Dataset** (`data/doctors.csv`): 16 specialized medical practitioners across 12 departments (Cardiology, Dermatology, Oncology, Neurology, Pediatrics, Orthopedics, Gastroenterology, Pulmonology, Endocrinology, Ophthalmology, Psychiatry, Nephrology).
- **Appointments Dataset** (`data/appointments.csv`): 35 clinical appointments linking patients and doctors with time slots and statuses (*Scheduled, Completed, Confirmed, Cancelled*).
- **Medical Records Dataset** (`data/medical_records.csv`): 25 in-depth clinical diagnosis notes.
- **Lab Reports Dataset** (`data/lab_reports.csv`): 20 laboratory test panels with diagnostic findings.
- **Treatments Dataset** (`data/treatments.csv`): 20 prescribed treatment and pharmaceutical regimens.
- **Schema & Seeding**: Updated in `lib/database_setup.sql` and `DatasetStore.java` with automatic PostgreSQL sync on connection.

---

### 2. Specialized String Matching Search Engine (`MedicalSearchEngine.java`)
Directly addresses pattern recognition and substring matching across all healthcare entities:
1. **Knuth-Morris-Pratt (KMP)**: Linear time $O(N + M)$ exact matching using the Longest Proper Prefix-Suffix (LPS) array.
2. **Rabin-Karp Rolling Hash**: Polynomial rolling hash with large prime modulus and collision verification.
3. **Boyer-Moore**: Sublinear average time $O(N / M)$ matching with Bad Character Heuristic shift table.
4. **Z-Algorithm**: Linear time $O(N + M)$ pattern search using the Z-array.
5. **Aho-Corasick Automaton**: Multi-pattern trie-based search with failure transitions for simultaneous multi-symptom lookup.
6. **Fuzzy Levenshtein Distance**: Approximate string matching tolerating typos and spelling errors (e.g. searching *"diabtes"* finds *"Type 2 Diabetes Mellitus"* with similarity score).
7. **Benchmark Suite**: Real-time performance profiling comparing match counts, character comparisons, and execution duration in microseconds ($\mu s$).

**Search Scope Available:**
- Search Doctors (by Name, Specialization, Department, Availability)
- Search Patients (by Name, Phone, Age, Gender, ID)
- Search Appointments (by Patient, Doctor, Date, Status)
- Search Medical Records (by Diagnosis, Symptoms, Clinical Notes)
- Global Search across all entities simultaneously.

---

### 3. Core Bugs & Stability Issues Resolved
- **Database Connection Crash**: In the original code, `DBConnection.java` threw `IllegalStateException` whenever `MEDSEARCH_DB_PASSWORD` was not set, causing all operations to fail. Replaced with an intelligent dual-mode storage engine:
  - If PostgreSQL is connected: operations sync to PostgreSQL and auto-create tables.
  - If PostgreSQL is not running or unconfigured: seamlessly operates using the high-performance integrated dataset store with zero crashes.
- **Scanner `InputMismatchException`**: Fixed unsafe `sc.nextInt()` calls across `M2_DocumentSimilarity.java` and management modules; implemented robust numeric validation.
- **Doctor Portal Enhancement**: Doctors can now view their own scheduled appointments (`viewByDoctor(doctorId)`) and update appointment statuses (`Scheduled` $\rightarrow$ `Completed` / `Confirmed` / `Cancelled`).
- **Greedy Activity Selection (`ActivitySelection.java`)**: Replaced hardcoded dummy arrays with greedy interval scheduling on clinical consultation slots sorted by finish time.
- **Network Algorithms (`M4_NetworkAlgorithms.java`)**: Removed Java generic array compiler warnings and dynamically wired doctor-patient bipartite matching to the live dataset.
- **Dynamic Programming (`M3_DynamicProgramming.java`)**: Resolved case-sensitivity discrepancies between Levenshtein, Needleman-Wunsch, and Smith-Waterman.
- **Medical Statistics (`MedicalStatistics.java`)**: Eliminated potential SegmentTree array index bounds exception when appointment count is 0; expanded analytics with patient demographics, appointment distributions, and diagnosis frequencies.

---

## How to Run

### Quick Run (PowerShell):
```powershell
.\compile_and_run.ps1
```
Or directly:
```powershell
javac -cp "lib/postgresql-42.7.11.jar" *.java
java -cp ".;lib/postgresql-42.7.11.jar" Main
```

### Optional PostgreSQL Setup:
If using PostgreSQL:
1. Create database: `CREATE DATABASE medsearch;`
2. Run `lib/database_setup.sql` in pgAdmin / psql.
3. Set password in PowerShell before running:
   ```powershell
   $env:MEDSEARCH_DB_PASSWORD="your_postgres_password"
   ```
*(Note: If PostgreSQL is not running, MedSearch automatically runs in high-performance integrated dataset mode with full capabilities.)*

---

## Default Demo Logins

- **Patient Portal**: ID `101`, Password: `pass123` *(or any ID 101 to 125)*
- **Doctor Portal**: ID `201`, Password: `doctor123` *(or any ID 201 to 216)*
- **Admin Portal**: Username: `admin1`, Password: `admin123`
- **Manager Portal**: Username: `manager1`, Password: `manager123`
