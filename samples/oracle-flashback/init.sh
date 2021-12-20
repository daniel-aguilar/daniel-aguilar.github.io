#!/bin/bash
docker run -d --name oracle-db \
       -v $(pwd)/setup:/opt/oracle/scripts/setup \
       oracle/database:19.3.0-ee
