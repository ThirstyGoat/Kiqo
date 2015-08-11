#!/bin/bash

# Usage: ./git-deliverable.sh [-G] [-M]
# -G skips git commands "git checkout master" and "git pull"
# -m skips maven commands "mvn clean package site"

# Requires git, mvn, mysql
# Requires internet access (for git, mvn and mysql)
# Assumes current directory is the git repo

# TODO: As of 11/8/15, the README mention of the jarfile needs to be manually bumped
# to the new version number (and committed to master) before this script is run.

#### PRECONFIGURATION
# Constants
GIT_REPO=`pwd`
OUT_DIRECTORY=`pwd`/deliverable

# Defaults
DO_GIT_MASTER_PULL=1
DO_MAVEN_BUILD=1

# http://wiki.bash-hackers.org/howto/getopts_tutorial
while getopts ":GM" opt; do
  case $opt in
    G) # don't do git stuff
      echo "-G: Skipping git stuff"
      DO_GIT_MASTER_PULL=0
      ;;
    M) # don't do maven stuff
      echo "-M : Skipping maven stuff"
      DO_MAVEN_BUILD=0
      ;;
    \?)
      echo "Invalid option: -$OPTARG"
      ;;
  esac
done

if [[ $DO_GIT_MASTER_PULL -gt 0 ]]; then
  echo "git checkout master"
  git checkout master
  echo "git pull"
  git pull
fi
if [[ $DO_MAVEN_BUILD -gt 0 ]]; then
  echo "mvn clean package site"
  mvn clean package site
fi

#### PACKAGING
# clear old deliverable if it exists
rm -rf $OUT_DIRECTORY
mkdir $OUT_DIRECTORY

echo "\nREADME.md" # - A readme that describes how to run your program and any information that you think is relevant
cp $GIT_REPO/README* $OUT_DIRECTORY

echo "source.zip" # - An archive of your source code
(cd $GIT_REPO; git archive --format zip --output $OUT_DIRECTORY/source.zip HEAD)

echo "\nprogram/" # - Your jar file and any other resources it needs
mkdir $OUT_DIRECTORY/program
cp $GIT_REPO/target/*.jar $OUT_DIRECTORY/program # TODO don't copy unshaded jar
# includes demo.json
cp -r $GIT_REPO/target/deploy/* $OUT_DIRECTORY/program
chmod +x $OUT_DIRECTORY/program/*.sh

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

echo "doc/" # - User and design documentation
mkdir $OUT_DIRECTORY/doc
ln -s $OUT_DIRECTORY/site/*.pdf $OUT_DIRECTORY/doc
mkdir $OUT_DIRECTORY/doc/javadoc
ln -s $OUT_DIRECTORY/site/apidocs $OUT_DIRECTORY/doc/javadoc
ln -s $OUT_DIRECTORY/site/testapidocs $OUT_DIRECTORY/doc/javadoc

#### COMPRESS TO ZIP
rm -rf $OUT_DIRECTORY.zip
zip -rq $OUT_DIRECTORY $OUT_DIRECTORY

echo "Done. Deliverable available at $OUT_DIRECTORY.zip"
echo "Remember to tag the repo!"
echo "  git tag -a sprint_X -m \"Deliverable for sprint X\""
echo "  git push origin --tags"
