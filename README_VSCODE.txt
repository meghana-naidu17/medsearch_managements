MEDSEARCH - HUMAN MEDICAL MANAGEMENT SYSTEM (ENHANCED & REVIEW READY)

QUICK RUN
1. Open this folder in VS Code or Terminal.
2. Run .\compile_and_run.ps1 OR execute:
   javac -cp "lib/postgresql-42.7.11.jar" *.java
   java -cp ".;lib/postgresql-42.7.11.jar" Main

DATASET INCLUDED (data/ & lib/database_setup.sql):
- 25 Patients (IDs 101-125)
- 16 Doctors across 12 Departments (IDs 201-216)
- 35 Appointments (IDs 301-335)
- 25 Medical Records & Diagnoses (IDs 401-425)
- 20 Lab Reports (IDs 501-520)
- 20 Treatments (IDs 601-620)

STRING MATCHING SEARCH METHODS:
Option 5 in Main Menu:
1. Knuth-Morris-Pratt (KMP)
2. Rabin-Karp Rolling Hash
3. Boyer-Moore (Bad Character Heuristic)
4. Z-Algorithm
5. Aho-Corasick Multi-Pattern Search
6. Fuzzy Levenshtein Distance (Typo Tolerant)
7. Full Algorithm Benchmark Suite

SAMPLE LOGINS:
Patient: ID 101, password pass123 (or IDs 101-125)
Doctor:  ID 201, password doctor123 (or IDs 201-216)
Admin:   admin1 / admin123
Manager: manager1 / manager123

POSTGRESQL NOTES:
If PostgreSQL is configured: set $env:MEDSEARCH_DB_PASSWORD="your_password"
If PostgreSQL is not running or unconfigured: MedSearch runs seamlessly
in high-performance embedded mode with preloaded dataset and persistent CSV storage.
