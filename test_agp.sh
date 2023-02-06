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

setGradleVersion 6.8.3
testUnderAGPVersion 4.2.0

setGradleVersion 7.3.3
testUnderAGPVersion 7.2.0

setGradleVersion 7.4
testUnderAGPVersion 7.3.0

setGradleVersion 7.5
testUnderAGPVersion 7.4.0
