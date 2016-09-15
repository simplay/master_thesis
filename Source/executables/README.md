# Run the Pipeline

This directory contains all relevant executables and scripts to run the pipeline on any operating system.
Please note that he pipeline has several dependencies, as well as is not currently fully functional on Windows.

To run a custom dataset, please read the readme included in `./Data/`.

## Dependencies

+ **Java 8**
+ **Matlab**
+ **JRuby v 9.0.0.1** <= (optional)

## Usage

### Mac OS X & Linux with JRuby

Simply enter `ruby pipeline.rb` in your terminal and you'll be guided through the whole pipeline.

### Mac OS X & Linux with JRuby

Enter `./run.sh` in your terminal. Don't forget to `chmod +x run.sh` beforehand.
Currently, only the `core.jar` can be run.

### Windows

Double click the `run.bat` file and you'll be guided thorugh the while pipeline.
Currently, only the `core.jar` can be run.


## Known Issues

Some executables have to be recompiled: The `srsf` executable.
Some operating system require the os specific binary for computing optical flows
Matlab is required to extract the core data and to generate the segmentations
Currently, Kernighan-Lin is not linked in the pipeline and thus cannot be used via the guided run scripts, only by manually running it. See its README.
