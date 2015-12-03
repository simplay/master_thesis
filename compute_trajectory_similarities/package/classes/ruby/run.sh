#!/bin/sh
echo hint:  use -d for timestep size of 1
ruby -J-Xmx8000m compute_similarities.rb -l 1 $1 $2 $3 $4 $5 $6 $7 $8
