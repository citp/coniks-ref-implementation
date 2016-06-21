#!/bin/bash

# copy over the source from the branch
git checkout origin/master coniks_common/src/org/coniks/coniks_common
git checkout origin/master coniks_server/src/org/coniks/coniks_server
git checkout origin/master coniks_test_client/src/org/coniks/coniks_test_client
cp coniks_common/src/org/coniks/coniks_common/*.java src/org/coniks/coniks_common/
cp coniks_server/src/org/coniks/coniks_server/*.java src/org/coniks/coniks_server/
cp coniks_test_client/src/org/coniks/coniks_test_client/*.java src/org/coniks/coniks_test_client/
git rm -rf coniks_common
git rm -rf coniks_server
git rm -rf coniks_test_client