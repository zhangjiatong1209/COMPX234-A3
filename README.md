Client-Server File Transfer and Tuple Space Management System
This system consists of a client and a server. The client is responsible for reading multiple text files and sending their contents line by line to the server. The server receives client requests, manages the tuple space, and periodically outputs statistical information about the operations.
Function Overview
Client
The client program Client.java reads a set of text files (client_1.txt to client_10.txt).
It reads the contents of the files line by line and sends them to the server.
It receives responses from the server and prints both the requests and responses.
Server
The server program Server.java listens on port 9090, waiting for client connections.
It uses a thread pool to handle concurrent connections from multiple clients.
It supports three operations: PUT, READ, and GET to manage the tuple space.
It prints statistical information about the server operations every 10 seconds, including the number of tuples, average tuple size, number of operations, etc.
Tuple Class
Tuple.java defines the structure of a tuple, which contains an immutable key and a mutable value.
Code Structure
Client.java: Responsible for file reading and communication with the server.
Server.java: Handles client requests, manages the tuple space, and periodically outputs statistical information.
Tuple.java: Defines the structure and basic operations of a tuple.
Operation Instructions
PUT Operation
Format: PUT <key> <value>
Function: If the specified key does not exist in the tuple space, the key-value pair will be added to the tuple space; otherwise, an error message will be returned.
READ Operation
Format: READ <key>
Function: If the specified key exists in the tuple space, the corresponding key-value pair will be returned; otherwise, an error message will be returned.
GET Operation
Format: GET <key>
Function: If the specified key exists in the tuple space, the key-value pair will be removed and returned; otherwise, an error message will be returned.
Notes
Ensure that the client and server are on the same network, and the port (9090) listened by the server is not occupied by other programs.
The text files (client_1.txt to client_10.txt) to be sent by the client must exist in the running directory of the client program.
The size of the thread pool of the server is fixed at 10, which can be adjusted in Server.java according to actual needs.
Author Information
Author: Zhang Jiatong 20233006326
