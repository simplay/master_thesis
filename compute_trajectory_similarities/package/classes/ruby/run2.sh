#!/bin/sh
echo hint:  use -d for timestep size of 1
java -jar -Xmx8000m package/jar/compute_trajectory_similarities.jar -l 1 $1 $2 $3 $4 $5 $6 $7 $8
