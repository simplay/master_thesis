@echo off

:START
CLS
echo "Please enter the number of one the options below:"
echo "+ [-1] Exit program"
echo "+ [1]  Generate Flows"
echo "+ [2]  Extract Core Data"
echo "+ [3]  Compute Similarity Matrix"
echo "+ [4]  Run Spectral Clustering"
set /p input= "Selecting "

if %input% == -1 (
goto END
) else (
if %input% == 1 (
goto RUN_FLOW
) else (
if %input% == 2 (
goto RUN_EXTRACTION
) else (
if %input% == 3 (
goto RUN_CORE
) else (
if %input% == 4 (
goto RUN_SC
)
)
)
)
)

:RUN_FLOW
echo "Generating flows..."
PAUSE
goto START

:RUN_EXTRACTION
echo "Starting core data extracting..."
PAUSE
goto START

:RUN_CORE
echo "Starting core pipeline..."

echo "Which dataset should be used?"
set /p dataset= "Selecting: "

echo "Which similarity task should be run?"
set /p task= "Selecting: "

echo "Should the local variance be used?"
set /p var= "Selecting: "

echo "Which nearest neighbor mode should be used?"
set /p nnm= "Selecting: "

echo "How many nearest neighbors should be extracted?"
set /p nn= "Selecting: "

echo "What lambda value should be used?"
set /p lambda= "Selecting: "

echo "What probability value should be used?"
set /p prob= "Selecting: "

java -jar -Xmx16000m core.jar -d %dataset% -task %task% -var %var% -nnm %nnm% -nn %nn% -lambda %lambda% -prob %prob%

PAUSE
goto START

:RUN_SC
echo "Starting spectral clustering..."
PAUSE
goto START

:END
echo "Exiting program..."
PAUSE
