#!/bin/bash

# Fail on any error.
set -e

# Make sure the right tools are installed
echo y | ${ANDROID_HOME}/tools/bin/sdkmanager "build-tools;28.0.3"
${ANDROID_HOME}/tools/bin/sdkmanager --install "ndk;20.0.5594570" "cmake;3.10.2.4988404"

cd ${KOKORO_ARTIFACTS_DIR}/github/glass-enterprise-samples

( cd Camera2Sample          && ./gradlew build )
( cd CardSample             && ./gradlew build )
( cd GallerySample          && ./gradlew build )
( cd GestureLibrarySample   && ./gradlew build )
( cd QRCodeScannerSample    && ./gradlew build )
( cd VoiceRecognitionSample && ./gradlew build )
( cd WebRTCSample           && ./gradlew build )
( cd NotesSample           && ./gradlew build )

