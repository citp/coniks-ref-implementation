#CONIKS Common

Copyright (C) 2015 Princeton University.

http://www.coniks.org

##Introduction
These are the files common to the basic implementation of a CONIKS server and the simple CONIKS test client, and include the binaries for two supporting libraries: [Google Protobufs](https://github.com/google/protobuf/tree/master/java) and [Javatuples](https://github.com/javatuples/javatuples).

##Using the Common Files
### Protobufs
CONIKS uses Google Protobufs to define the message format for all communication between the test client and the basic server. Since the Protos Java files are generated by compiling the .proto files, you should never directly edit them! If you wish to modify the format of a specific message, do so in the appropriate .proto source file and recompile it.

### Building
All common files (including the supporting libraries) are compiled when either component of the CONIKS system (the server or the client) is compiled. As part of the compilation process for a given CONIKS component, the compiled common files are bundled automatically with the component: the Makefile for the given component places the *coniks_common* and library packages in the same location as the component's .class files. This way each component can be built separately while sharing the common files.

**Note: If you have edited/recompiled any of the common Java files in this package, you will need to rebuild both the client and the server to ensure that the common files remain consistent between both components.**

##Documentation
[Read the common files' Java API (javadoc)](https://coniks-sys.github.io/coniks-ref-implementation/org/coniks/coniks_common/package-summary.html)