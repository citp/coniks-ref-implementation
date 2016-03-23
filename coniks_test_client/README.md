#CONIKS Test Client

Copyright (C) 2015 Princeton University.

<<<<<<< HEAD
https://coniks.cs.princeton.edu

##Introduction
This is a simple test client for the CONIKS key management service. It supports new key registrations, key lookups and key consistency checks. It is designed to communicate with the basic implementation of a [CONIKS server](https://github.com/citp/coniks-ref-implementation/tree/master/coniks_server).
=======
http://www.coniks.org

##Introduction
CONIKS is a key management service that provides consistency and privacy for end-user public keys. It protects users against malicious or coerced key servers which may want to impersonate these users to compromise their secure communications: CONIKS will quickly detect any spurious keys, or any versions of the key directory that are inconsistent between two or more users. Nonetheless, CONIKS users do not need to worry about or even see these protocols, or the encryption keys, as CONIKS seamlessly integrates into any existing secure messaging application.

This is a simple test client that supports new key registrations, key lookups and key consistency checks. It is designed to communicate with the basic implementation of a CONIKS server.
>>>>>>> Adding the new files

##Using the Test Client

###Preparing SSL
CONIKS clients communicate via SSL/TLS connections with any CONIKS server.
Here are instructions for creating the trusted certificate store for your client. You will have to manually import each server certificate the client will communicate with beforehand.
Repeat the following steps for each server certificate:
```
keytool -import -alias <alias> -file <certificate file> -keystore <truststore name>
```
You will be asked to enter a password for the truststore. Make sure you remember this password.

###Client Configuration
In *ClientConfig.java*: Set the port number, the absolute path to your trusted certificate store, and the truststore password in the ```ClientConfig()``` constructor.

###Building
We understand that people may not necessarily want to build and run the client on the same machine. 
- Compiling: 
In the *Makefile*, set the directory where you want the compiler to place the class files in **CLASS_DEST**. Then run:
```
make
```
- Pushing the compiled code to a remote machine:
In the *Makefile*, set the **PUBUSER**, **PUBHOST**, and **PUBPATH** variables to the appropriate values. Then run:
```
make pubbin
```
This step assumes the **PUBUSER** has ssh access to the remote machine **PUBHOST**.
- Pushing the run script to a remote machine:
```
make pubscr
```
This step also assumes the **PUBUSER** has ssh access to the remote machine **PUBHOST**, and may require you to change the permissions of the script on the remote host.

###Running
We provide a run script for the CONIKS test client *coniks_test_client.sh*, which accepts 
multiple commands to test the various operations done by the client.

The test client supports three commands: 
- ```REGISTER```: register a new name-to-public key binding.
- ```LOOKUP```: looki up a public key, and obtaining the proof of the binding's validity.
- ```VERIFY```: verify a consistency proof for a key binding. 

In addition to specifying your CONIKS key server's hostname, you may specify the number of times to perform the operation; for the i-th  iteration, the command will be performed for a test username of the form "*test-i*". Since you may want to perform operations on a subset of users or add more to the existing ones in the key server's directory, you may also specify an offset to the iteration counter. Lastly, for the ```VERIFY``` command, the client also accepts a fourth argument, verbose (set to 1 to turn on this flag).

In general, the client is run as follows:
```
./coniks_test_client.sh <hostname> <cmd> [<num-iters>] [<offset>] [<verbose>]
```

Some examples for running the client:

The example will register 10 new users at an offset of 10 (e.g. assuming that you have previously added 10 users to the key directory):
```
./coniks_test_client.sh <hostname> REGISTER 10 10
```

This example will lookup user *test-18*'s public key:
```
./coniks_test_client.sh <hostname> LOOKUP 1 18
```

This example will verify the consistency proof obtained from looking up user *test-7*'s through user *test-10*'s keys, with verbose output:
```
./coniks_test_client.sh <hostname> VERIFY 4 7 1
```

##Documentation
<<<<<<< HEAD
[Read the test client's Java API (javadoc)](https://citp.github.io/coniks-ref-implementation/org/coniks/coniks_test_client/package-summary.html)
=======
[Read the test client's Java API (javadoc)](https://coniks-sys.github.io/coniks-ref-implementation)
>>>>>>> Adding the new files