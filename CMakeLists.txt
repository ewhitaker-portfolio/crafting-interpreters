cmake_minimum_required(VERSION 3.26)

set(CMAKE_BUILD_TYPE "Debug")
set(CMAKE_C_STANDARD 23)
set(CMAKE_C_COMPILER "clang")

project(crafting-interpreters VERSION 0.0.1 LANGUAGES C)

file(GLOB_RECURSE INCLUDES src/*.h)
file(GLOB_RECURSE SOURCES src/*.c)

add_executable(crafting-interpreters ${INCLUDES} ${SOURCES})

target_include_directories(
    crafting-interpreters PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/c/include/crafting-interpreters
)
file(GLOB_RECURSE PUBLIC_INCLUDES src/main/c/include/crafting-interpreters/*.h)
target_sources(
    crafting-interpreters INTERFACE
    FILE_SET HEADERS
    BASE_DIRS ${CMAKE_CURRENT_SOURCE_DIR}/src/main/c/include
    FILES ${PUBLIC_INCLUDES}
)

install(TARGETS crafting-interpreters RUNTIME FILE_SET HEADERS)