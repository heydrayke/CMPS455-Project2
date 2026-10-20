# CMPS455-Project2
Simulated an operating system's protection mechanism in Java using threads. Each thread acts as a user in a domain and makes random requests to read files, write files, or switch domains. An arbitrator grants or denies each request using one of three access control schemes: an Access Matrix, Access Lists for Objects, or Capability Lists for Domains. Semaphores protect file access so no file is read while being written to, and vice versa.


## To compile and run from command line:
####
    1.) javac Main.java
        (compile program and classes)
####
    2.) java Main -S 1
        (runs Task 1: Access Matrix)
####
    3.) java Main -S 2
        (runs Task 2: Access List for Objects)
####
    4.) java Main -S 3
        (runs Task 3: Capability List for Domains)






## To run without compiling first (Java 11+, no .class files):
####
    java Main.java -S 1
        (replace 1 with 2 or 3 to select the task)


## To run in IntelliJ:
####
    Run > Edit Configurations > Program Arguments: -S 1
        (replace 1 with 2 or 3 to select the task)

