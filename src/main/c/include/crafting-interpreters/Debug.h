#ifndef CRAFTING_INTERPRETERS_DEBUG_H
#define CRAFTING_INTERPRETERS_DEBUG_H

#include "Chunk.h"

#ifdef __cplusplus
extern "C" {
#endif

void disassembleChunk(Chunk* chunk, const char* name);
int disassembleInstruction(Chunk* chunk, int offset);

#ifdef __cplusplus
}
#endif

#endif
