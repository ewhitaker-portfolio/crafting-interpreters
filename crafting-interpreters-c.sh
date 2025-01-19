#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

rm --recursive --force ./target

cmake -DCMAKE_EXPORT_COMPILE_COMMANDS=ON -G Ninja -S . -B ./target
mv --update ./target/compile_commands.json ./compile_commands.json
cmake --build ./target

exec ./target/crafting-interpreters "$@"
