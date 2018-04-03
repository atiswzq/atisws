rm -rf release/dependency/
mkdir -p release/dependency/
mvn clean
mvn scala:compile compile
mvn dependency:copy-dependencies
rm -rf  release/classes
mkdir -p release/classes
cp -rf target/dependency/* release/dependency/
cp -rf target/classes release