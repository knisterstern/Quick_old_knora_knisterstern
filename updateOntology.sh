#!/usr/bin/env bash
cd ./Knora/Knora/webapi/scripts && ./graphdb-free-init-knora-test.sh
cd ../../../..
docker restart $(docker ps -q --filter ancestor=nieine/knora )