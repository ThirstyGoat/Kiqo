#!/bin/bash

# Required: git, mysql

pwd
GIT_REPO=$1
(cd $GIT_REPO; pwd)
if [ ! -d $1 ]
then
  echo "git repo must be supplied as positional argument"
  exit 1
fi

OUT_DIRECTORY=`pwd`/deliverable
#http://wiki.bash-hackers.org/howto/getopts_tutorial
while getopts o:m option
do
  case "${option}"
  in
    o)
      OUT_DIRECTORY=${OPTARG}  # NO TRAILING SLASH
    ;;
    m)
      mvn package site # generate required packages
    ;;
    \?)
      exit "Invalid option"
    ;;
  esac
done

#clear
rm -r $OUT_DIRECTORY
mkdir $OUT_DIRECTORY

echo "README" # - A read me that describes how to run your program and any information that you think is relevant
cp $GIT_REPO/README* $OUT_DIRECTORY

echo "source.zip" # - An archive of your source code"
(cd $GIT_REPO; git archive --format zip --output $OUT_DIRECTORY/source.zip HEAD)

echo "program/" # - Your jar file and any other resources it needs
mkdir $OUT_DIRECTORY/program
cp $GIT_REPO/target/*.jar $OUT_DIRECTORY/program # TODO don't copy unshaded jar
cp -r $GIT_REPO/target/deploy/* $OUT_DIRECTORY/program

echo "doc/" # - User and design documentation
mkdir $OUT_DIRECTORY/doc
cp $GIT_REPO/target/site/*.pdf $OUT_DIRECTORY/doc
mkdir $OUT_DIRECTORY/doc/javadoc
cp -r $GIT_REPO/target/site/apidocs/* $OUT_DIRECTORY/doc/javadoc

echo "manual_test_plans/" # - Any manual test plans you executed
mkdir $OUT_DIRECTORY/manual_test_plans
DB_NAME=akm96
DB_USER=akm96
DB_PASS=45698866
sql_query="SELECT * FROM Tests JOIN TestResults ON Tests.id = TestResults.testId ORDER BY Tests.id;"
# tab separated
mysql --host=mysql.cosc.canterbury.ac.nz --user=$DB_USER --password=$DB_PASS $DB_NAME --column-names --batch        <<< $sql_query > $OUT_DIRECTORY/manual_test_plans/manual_tests.tab
# html table
mysql --host=mysql.cosc.canterbury.ac.nz --user=$DB_USER --password=$DB_PASS $DB_NAME --column-names --batch --html <<< $sql_query > $OUT_DIRECTORY/manual_test_plans/manual_tests.html

echo "surefire-reports/" # - The surefire test output
mkdir $OUT_DIRECTORY/surefire-reports
cp -r $GIT_REPO/target/surefire-reports/* $OUT_DIRECTORY/surefire-reports

echo "site/" # - The maven site output
mkdir $OUT_DIRECTORY/site
cp -r $GIT_REPO/target/site/* $OUT_DIRECTORY/site
