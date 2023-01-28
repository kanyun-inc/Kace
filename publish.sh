getProp(){
   grep "${1}" gradle.properties | cut -d'=' -f2 | sed 's/\r//'
}
publishVersion=$(getProp VERSION_NAME)
snapshotSuffix='SNAPSHOT'

./gradlew publishAllPublicationsToMavenCentral
if [[ "$publishVersion" != *"$snapshotSuffix"* ]]; then
  echo "auto release artifacts of ${publishVersion}"
  ./gradlew closeAndReleaseRepository
fi