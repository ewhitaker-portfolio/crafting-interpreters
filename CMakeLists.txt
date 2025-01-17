cmake_minimum_required(VERSION 3.26)
project(crafting-interpreters C)

set(CMAKE_C_STANDARD 23)
set(CMAKE_C_COMPILER "clang")

file(GLOB_RECURSE SOURCES src/*.c)
add_executable(crafting-interpreters ${SOURCES})