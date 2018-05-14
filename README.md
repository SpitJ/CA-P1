# CA-P1 Julian Spit, Marvin Killer

Structure of the first Assignment
Language: Java
Database: Guava Table
Result presentation: CSV 

General Structure: 4 classes

- Run: Execute main class
- ReadXML: Read XML files and parse into Guava Table
- CalcYMatrix: Compute Y matrix through recursive function
- TableToCSV: Create output data - Grid Database, Admittance Table, Y Matrix

%%%%%%%% Run %%%%%%%%

1. Import XML files (Any possible EQ and SSH files work in the prorgam)
2. Read and Parse XML files into Guava Table with ReadXML class
3. Compute Y matrix with CalcYMatrix class
4. Write output data in CSV files with TableToCSV class

%%%%%%%% Internal data structure %%%%%%%%
The structure of the internal data is based on a google core library (GUAVA) Collection called Table.
This collection represents a Hashmap within a Hashmap, allowing fast and flexible indexing, adding of values, iterating etc.

When importing and parsing the XML files, the data is stored in a Table with all extracted information.
All internal data (intermediate and final) are stored under /csv for visitiblity.

