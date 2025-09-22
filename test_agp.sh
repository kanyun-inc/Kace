function setGradleVersion() {
  local distributionBase=https\://services.gradle.org/distributions/

  local GradleVersion=$1
  echo ./gradlew -PSetGradleVersion=true wrapper --gradle-distribution-url ${distributionBase}gradle-$GradleVersion-bin.zip
  ./gradlew -PSetGradleVersion=true wrapper --gradle-distribution-url ${distributionBase}gradle-$GradleVersion-bin.zip
}

function testUnderAGPVersion() {
  local TestAGPVersion=$1
  ./gradlew clean

  echo ./gradlew :app:assembleMinApi21DemoDebug -PtestAgp=true -PagpVersion=$TestAGPVersion
  ./gradlew :app:assembleMinApi21DemoDebug -PtestAgp=true -PagpVersion=$TestAGPVersion
}

cd kace-sample

setGradleVersion 8.5
testUnderAGPVersion 7.3.1

setGradleVersion 8.5
testUnderAGPVersion 7.4.0

setGradleVersion 8.5
testUnderAGPVersion 7.3.1

setGradleVersion 8.5
testUnderAGPVersion 8.0.0
