# FuzzyDB

Prototype for defining, storing and querying a relational database with fuzzy
attributes. It works as a translator of a SQL definition extended with syntax
for dealing with fuzzy attributes, to SQL as understood by PostgreSQL.

### Dependencies:
- PostgreSQL 9.3 or better.
  The translator uses several custom SQL extensions provided by Postgres,
  so it is pretty tightly coupled to that RDBMS.
- Java 1.7.
  The JSqlParser version our SQL parser is based on uses an internal 
  proprietary Java API, so it might not work on a more recent version.
- JavaCC for compiling the SQL parser.
- Ant for compiling the parser and the client. All required libraries are
  included within the repository.
- Bash for executing the script that compiles the parser and automatically
  updates its corresponding .jar library in the client.
  If you don't have bash, you can do it manually.

### Modifying the code:

This project is divided in two subprojects:
- Parser
  A fork of JSqlParser, modified with fuzzy syntax extensions.
- Client
  A console application that translates fuzzy SQL into SQL statements for
  Postgres.

Each project also has its Netbeans files, so you can import it in Netbeans
effortlessly.

### Big TODO:
- Move JSqlParser into a subrepo and make it easier to merge upstream changes.
