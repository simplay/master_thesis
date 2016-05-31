@echo off

:START
echo "Please enter the number of one the options below:"
echo "+ [-1] Exit program"
echo "+ [1]  Run core"
echo "+ [2]  run core"
echo "+ [3]  run core"
echo "+ [4]  run core"
set /p input= "Selecting "

if %input% == 1 (
echo "running core"
goto RUN_CORE
) else (
if %input% == -1 (
echo "Exiting program..."
goto END
)
)


:RUN_CORE
echo "foobar"
goto START

:END
pause
