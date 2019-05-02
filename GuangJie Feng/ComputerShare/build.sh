#! /bin/bash

if [[ -e bin ]]; then
	rm -rf bin
fi

mkdir -p ./bin

JARFILE="computershare.jar"

javac -source 1.8 -target 1.8 -sourcepath ./src -d ./bin src/io/gfeng/app/WithdrawApp.java

jar cfe $JARFILE io.gfeng.app.WithdrawApp  -C ./bin .


echo "Build Completed, packed in $JARFILE"

if [ -f $JARFILE ]; then
	echo "Test Begin"
	java -jar $JARFILE -test
	echo "Test Completes"
	
	echo ""

	java -jar $JARFILE
else
	echo "The file $JARFILE is not found."
	echo "Build Fail"
fi


