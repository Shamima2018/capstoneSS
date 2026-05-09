#!/bin/bash

echo "=== Bookstore Practice Codebase ==="
echo ""
echo "Compiling..."
javac -d bin src/com/bookstore/*.java src/com/bookstore/models/*.java src/com/bookstore/utils/*.java 2>&1

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Running..."
    java -cp bin com.bookstore.Main
else
    echo "Compilation failed!"
    exit 1
fi
