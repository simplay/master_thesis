#!/bin/sh
echo "Computing flow files..."
ruby -J-Xmx8000m run.rb $1 $2 $3 $4 $5
