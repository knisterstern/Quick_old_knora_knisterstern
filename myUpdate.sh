#!/usr/bin/env bash
cd ./Knora/Knora/webapi/scripts && ./my-init.sh
cd ../../../..
docker restart $(docker ps -q --filter ancestor=nieine/knora )
