#!/bin/bash
#
# Packages the built Distributor artefacts (core jar, sample CLI/GUI launchers,
# and logging config) into a single self-extracting installer using makeself.
#
# Usage:
#   ./build-installer.sh [--build]
#
#   --build   Run 'gradle build' first instead of using the jar already in ./libs
#
set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INSTALLER_SRC="$ROOT_DIR/installer"
STAGE_DIR="$ROOT_DIR/installer-build/stage"
OUT_DIR="$ROOT_DIR/installer-build"

if [ "$1" == "--build" ]; then
    if ! command -v gradle >/dev/null 2>&1; then
        echo "ERROR: --build requested but 'gradle' was not found on PATH." >&2
        exit 1
    fi
    echo "Building Distributor with gradle..."
    (cd "$ROOT_DIR" && gradle build)
fi

if ! command -v makeself >/dev/null 2>&1; then
    echo "ERROR: 'makeself' is required to build the installer but was not found on PATH." >&2
    exit 1
fi

DISTRIBUTOR_JAR="$(ls -t "$ROOT_DIR"/libs/distributor-*.jar 2>/dev/null | head -n1)"
if [ -z "$DISTRIBUTOR_JAR" ]; then
    echo "ERROR: no distributor-*.jar found in $ROOT_DIR/libs. Run './build-installer.sh --build' or build with gradle first." >&2
    exit 1
fi
echo "Using artefact: $DISTRIBUTOR_JAR"

VERSION="$(python3 -c "import json;d=json.load(open('$ROOT_DIR/version.json'));print(f\"{d['major']}.{d['minor']}\")" 2>/dev/null || echo "0.0")"

rm -rf "$STAGE_DIR"
mkdir -p "$STAGE_DIR/libs" "$STAGE_DIR/config"

cp "$DISTRIBUTOR_JAR" "$STAGE_DIR/libs/"
cp -r "$INSTALLER_SRC/bin" "$STAGE_DIR/bin"
cp "$ROOT_DIR/log4j.xml" "$ROOT_DIR/distributor-log4j.xml" "$STAGE_DIR/config/"
cp "$INSTALLER_SRC/setup.sh" "$STAGE_DIR/setup.sh"
chmod +x "$STAGE_DIR/bin"/*.sh "$STAGE_DIR/setup.sh"

OUT_FILE="$OUT_DIR/distributor-$VERSION-install.run"

makeself --gzip "$STAGE_DIR" "$OUT_FILE" "Distributor $VERSION Installer" ./setup.sh

echo
echo "Installer created: $OUT_FILE"
echo "Install with:  $OUT_FILE [install_dir]   (default install dir: \$HOME/distributor)"
