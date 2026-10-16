$jar = "lib/postgresql-42.7.11.jar"
javac -cp $jar *.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp ".;$jar" Main
