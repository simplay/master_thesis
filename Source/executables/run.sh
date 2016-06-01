#!/bin/sh

until [ $answer -eq -1 ]; do
clear
echo "Please enter the number of one the options below:"
echo "+ [-1] Exit program"
echo "+ [1]  Generate Flows"
echo "+ [2]  Extract Core Data"
echo "+ [3]  Compute Similarity Matrix"
echo "+ [4]  Run Spectral Clustering"

read answer
echo Ihre Antwort war: $answer

if [ $answer -eq 1 ]
  then 
    echo FOOBAR
    echo BBB
    read
  elif [ $answer -eq 2 ]
  then 
    echo BAZ
    echo BAZ
    read
  elif [ $answer -eq 3 ]
  then 
    echo "Starting core pipeline..."
    
    echo "Which dataset should be used?"
    read dataset
    
    echo "Which similarity task should be run?"
    read task
    
    echo "Should the local variance be used?"
    read var
    
    echo "Which nearest neighbor mode should be used?"
    read nnm
    
    echo "How many nearest neighbors should be extracted?"
    read nn
    
    echo "What lambda value should be used?"
    read lambda
    
    echo "What probability value should be used?"
    read prob
    
    java -jar -Xmx16000m core.jar -d $dataset -task $task -var $var -nnm $nnm -nn $nn -lambda $lambda -prob $prob
    
    echo Please press any key to continue...
    read
  elif [ $answer -eq 4 ]
  then 
    echo BAZ
    echo BAZ
    read
  elif [ $answer -eq -1 ]
  then
    echo Exiting Program...
    read
  else echo Unknown Input; read;
fi
done
