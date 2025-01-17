#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"
rm --recursive --force ./target
cmake -G Ninja -S . -B ./target
cmake --build ./target

exec ./target/crafting-interpreters