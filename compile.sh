#!/bin/bash
echo "Compiling Assessment Feedback System..."

if [ ! -d "bin" ]; then
    mkdir bin
fi

javac -d bin src/utils/*.java src/common/*.java src/admin/*.java src/leader/*.java src/lecturer/*.java src/student/*.java src/Main.java

if [ $? -eq 0 ]; then
    echo ""
    echo "Compilation successful!"
    echo "Run './run.sh' to start the application."
else
    echo ""
    echo "Compilation failed! Please check for errors."
fi
