#ifndef CRAFTING_INTERPRETERS_VM_H
#define CRAFTING_INTERPRETERS_VM_H

#include "Chunk.h"

#ifdef __cplusplus
extern "C" {
#endif

#define STACK_MAX 256

typedef struct {
    Chunk* chunk;
    uint8_t* ip;
    Value stack[STACK_MAX];
    Value* stackTop;
} VM;

typedef enum {
    INTERPRET_OK,
    INTERPRET_COMPILE_ERROR,
    INTERPRET_RUNTIME_ERROR
} InterpretResult;

void initVM();
void freeVM();
InterpretResult interpret(const char* source);
void push(Value value);
Value pop();

#ifdef __cplusplus
}
#endif

#endif
