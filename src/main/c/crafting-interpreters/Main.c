#include <stdlib.h>
#include <stdio.h>

#include "Common.h"
#include "Chunk.h"
#include "Debug.h"

#ifdef __cplusplus
extern "C" {
#endif

int main(void) {
    setbuf(stdout, NULL);
    setbuf(stderr, NULL);

    Chunk chunk;
    initChunk(&chunk);

    int constant  = addConstant(&chunk, 1.2);
    writeChunk(&chunk, OP_CONSTANT, 123);
    writeChunk(&chunk, constant, 123);

    writeChunk(&chunk, OP_RETURN, 123);

    disassembleChunk(&chunk, "test chunk");
    freeChunk(&chunk);
    return EXIT_SUCCESS;
}

#ifdef __cplusplus
}
#endif
