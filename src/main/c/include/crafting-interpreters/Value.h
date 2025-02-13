#ifndef CRAFTING_INTERPRETERS_VALUE_H
#define CRAFTING_INTERPRETERS_VALUE_H

#ifdef __cplusplus
extern "C" {
#endif

typedef double Value;

typedef struct {
    int capacity;
    int count;
    Value* values;
} ValueArray;

void initValueArray(ValueArray* array);
void writeValueArray(ValueArray* array, Value value);
void freeValueArray(ValueArray* array);
void printValue(Value value);

#ifdef __cplusplus
}
#endif

#endif
