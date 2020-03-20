#!/bin/bash

# Fail on any error.
set -e

cd ${KOKORO_ARTIFACTS_DIR}/github/glass-enterprise-samples

( cd Camera2Sample          && ./gradlew build )
( cd CardSample             && ./gradlew build )
( cd GallerySample          && ./gradlew build )
( cd GestureLibrarySample   && ./gradlew build )
( cd QRCodeScannerSample    && ./gradlew build )
( cd VoiceRecognitionSample && ./gradlew build )
