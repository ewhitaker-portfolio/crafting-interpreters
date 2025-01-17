#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"
cmake -G Ninja -S . -B ./target
cmake --build ./target