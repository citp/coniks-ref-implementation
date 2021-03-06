#
# define compiler and compiler flag variables
#

# change this if building the server somewhere else
CLASS_DEST=bin
# Do not modify SRC
SRC=src
# Do not modify COMMON
COMMON=../coniks_common/src
# This is needed so the appropriate common files are included in the compilation
JFLAGS=-d $(CLASS_DEST) -cp $(CLASS_DEST) -sourcepath $(SRC) -g
JC=javac
SCP=rsync
SCPFLAGS=-rz
PUBUSER=user
PUBHOST=remote.host
#This should be an absolute path
PUBPATH=/path/to/server/on/remote

#
# Clear any default targets for building .class files from .java files; we 
# will provide our own target entry to do this in this makefile.
# make has a set of default targets for different suffixes (like .c.o) 
# Currently, clearing the default for .java.class is not necessary since 
# make does not have a definition for this target, but later versions of 
# make may, so it doesn't hurt to make sure that we clear any default 
# definitions for these
#

.SUFFIXES: .java .class


#
# Here is our target entry for creating .class files from .java files 
# This is a target entry that uses the suffix rule syntax:
#	DSTS:
#		rule
#  'TS' is the suffix of the target file, 'DS' is the suffix of the dependency 
#  file, and 'rule'  is the rule for building a target	
# '$*' is a built-in macro that gets the basename of the current target 
# Remember that there must be a < tab > before the command line ('rule') 
#

.java.class: 
	$(JC) $(JFLAGS) $*.java


#
# CLASSES is a macro consisting of each java source file
#

CLASSES = ./src/org/coniks/coniks_server/TreeBuilder.java \
	./src/org/coniks/coniks_server/LeafNode.java \
	./src/org/coniks/coniks_server/UserLeafNode.java \
	./src/org/coniks/coniks_server/PaddingLeafNode.java \
	./src/org/coniks/coniks_server/RootNode.java \
	./src/org/coniks/coniks_server/InteriorNode.java \
	./src/org/coniks/coniks_server/TreeNode.java \
	./src/org/coniks/coniks_server/DirectoryOps.java \
	./src/org/coniks/coniks_server/TransparencyOps.java \
	./src/org/coniks/coniks_server/RequestHandler.java \
	./src/org/coniks/coniks_server/ServerConfig.java \
	./src/org/coniks/coniks_server/ServerHistory.java \
	./src/org/coniks/coniks_server/ServerLogger.java \
	./src/org/coniks/coniks_server/ServerMessaging.java \
	./src/org/coniks/coniks_server/ServerUtils.java \
	./src/org/coniks/coniks_server/SignatureOps.java \
	./src/org/coniks/coniks_server/SignedTreeRoot.java \
	./src/org/coniks/coniks_server/KeyOps.java \
	./src/org/coniks/coniks_server/Operation.java \
	./src/org/coniks/coniks_server/KeyChange.java \
	./src/org/coniks/coniks_server/Register.java \
	./src/org/coniks/coniks_server/MsgHandlerLogger.java \
	./src/org/coniks/coniks_server/TimerLogger.java \
	./src/org/coniks/coniks_server/DebugLogger.java \
	./src/org/coniks/coniks_server/ConiksServer.java

#
# the default make target entry
#

default: common server

#
# This target entry uses Suffix Replacement within a macro: 
# $(name:string1=string2)
# 	In the words in the macro named 'name' replace 'string1' with 'string2'
# Below we are replacing the suffix .java of all words in the macro CLASSES 
# with the .class suffix
#

common:
	mkdir -p $(CLASS_DEST)
	$(JC) -d $(CLASS_DEST) -cp $(COMMON) $(COMMON)/org/coniks/coniks_common/*.java
	cp -r $(COMMON)/com $(CLASS_DEST)
	cp -r $(COMMON)/org/javatuples $(CLASS_DEST)/org

server: $(CLASSES:.java=.class)

pubbin:
	@echo generating server directory hierarchy
	ssh $(PUBUSER)@$(PUBHOST) mkdir -p $(PUBPATH)/org/coniks/coniks_server
	ssh $(PUBUSER)@$(PUBHOST) mkdir -p $(PUBPATH)/org/coniks/coniks_common
	@echo generating protos directory hierarchy
	ssh $(PUBUSER)@$(PUBHOST) mkdir -p $(PUBPATH)/com/google/protobuf
	@echo generating javatuples directory hierarchy
	ssh $(PUBUSER)@$(PUBHOST) mkdir -p $(PUBPATH)/org/javatuples/valueintf
	@echo sending CONIKS server files
	rsync -rz --progress --exclude '*~' $(CLASS_DEST)/org/coniks/coniks_server/*.class $(PUBUSER)@$(PUBHOST)\:$(PUBPATH)/org/coniks/coniks_server/
	rsync -rz --progress --exclude '*~' $(CLASS_DEST)/org/coniks/coniks_common/*.class $(PUBUSER)@$(PUBHOST)\:$(PUBPATH)/org/coniks/coniks_common/
	@echo sending protobufs
	rsync -rz --progress --exclude '*~' $(CLASS_DEST)/com/google/protobuf/*.class $(PUBUSER)@$(PUBHOST)\:$(PUBPATH)/com/google/protobuf
	@echo sending javatuples
	rsync -rz --progress --exclude '*~' $(CLASS_DEST)/org/javatuples/*.class $(PUBUSER)@$(PUBHOST)\:$(PUBPATH)/org/javatuples/
	rsync -rz --progress --exclude '*~' $(CLASS_DEST)/org/javatuples/valueintf/*.class $(PUBUSER)@$(PUBHOST)\:$(PUBPATH)/org/javatuples/valueintf/

pubscr: 
	rsync -rz --progress --exclude '*~' ./coniks_server.sh $(PUBUSER)@$(PUBHOST)\:$(PUBPATH)/

#
# RM is a predefined macro in make (RM = rm -f)
#

clean: 
	$(RM) -r $(CLASS_DEST)
