# peerless.java
This java program was written for my Multithreaded Distributed Programming course at SCSU, Fall 2014 semester.
It provides the user with a GUI with the following options: (1) Search for a given file among all currently online IP/ports
(2) Directory listing for any online IP/port (3) download of a spcific file once located (4) optional appending of a prefix
for downloaded file. One highlight of the system is that no central node, listing all enrolled peers, is required. A new user
simply must supply the IP/port of an already enrolled peer, then assuming that peer is online the entire current list is 
transferred to the new peer. Additional, any time a peer attaches to the network, the peer's list is refreshed using the 
longest list available.

Note 1: The system only supports the Windows loopback address 127.0.0.1
