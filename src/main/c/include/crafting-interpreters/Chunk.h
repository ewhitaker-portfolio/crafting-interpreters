#ifndef CRAFTING_INTERPRETERS_CHUNK_H
#define CRAFTING_INTERPRETERS_CHUNK_H

#include "Common.h"
#include "Value.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum {
    OP_CONSTANT,
    OP_ADD,
    OP_SUBTRACT,
    OP_MULTIPLY,
    OP_DIVIDE,
    OP_NEGATE,
    OP_RETURN,
} OpCode;

typedef struct {
    int count;
    int capacity;
    uint8_t* code;
    int* lines;
    ValueArray constants;
} Chunk;

void initChunk(Chunk* chunk);
void freeChunk(Chunk* chunk);
void writeChunk(Chunk* chunk, uint8_t byte, int line);
int addConstant(Chunk* chunk, Value value);

#ifdef __cplusplus
}
#endif

#endif
