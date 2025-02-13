#include <stdlib.h>
#include <stdio.h>

#include "Chunk.h"
#include "Debug.h"
#include "VM.h"

#ifdef __cplusplus
extern "C" {
#endif

int main(void) {
    setbuf(stdout, NULL);
    setbuf(stderr, NULL);

    initVM();

    Chunk chunk;
    initChunk(&chunk);

    int constant  = addConstant(&chunk, 1.2);
    writeChunk(&chunk, OP_CONSTANT, 123);
    writeChunk(&chunk, constant, 123);

    constant = addConstant(&chunk, 3.4);
    writeChunk(&chunk, OP_CONSTANT, 123);
    writeChunk(&chunk, constant, 123);

    writeChunk(&chunk, OP_ADD, 123);

    constant = addConstant(&chunk, 5.6);
    writeChunk(&chunk, OP_CONSTANT, 123);
    writeChunk(&chunk, constant, 123);

    writeChunk(&chunk, OP_DIVIDE, 123);
    writeChunk(&chunk, OP_NEGATE, 123);

    writeChunk(&chunk, OP_RETURN, 123);

    disassembleChunk(&chunk, "test chunk");
    interpret(&chunk);
    freeVM();
    freeChunk(&chunk);
    return EXIT_SUCCESS;
}

#ifdef __cplusplus
}
#endif
