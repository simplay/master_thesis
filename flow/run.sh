#!/bin/sh
echo "Computing flow files..."
ruby -J-Xmx8000m run.rb $1
