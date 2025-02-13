#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sysexits.h>

#include "VM.h"

#ifdef __cplusplus
extern "C" {
#endif

static void repl() {
    char line[1024];
    for (;;) {
        printf("> ");

        if (!fgets(line, sizeof(line), stdin)) {
            printf("\n");
            break;
        }

        interpret(line);
    }
}

static char* readFile(const char* path) {
    FILE* file = fopen(path, "rb");
    if (file == NULL) {
        fprintf(stderr, "Could not open file \"%s\".\n", path);
        exit(EX_IOERR);
    }

    fseek(file, 0L, SEEK_END);
    size_t fileSize = ftell(file);
    rewind(file);

    char* buffer = (char *)malloc(fileSize + 1);
    if (file == NULL) {
        fprintf(stderr, "Not enough memory to read \"%s\".\n", path);
        exit(EX_IOERR);
    }
    size_t bytesRead = fread(buffer, sizeof(char), fileSize, file);
    if (file == NULL) {
        fprintf(stderr, "Could not read file \"%s\".\n", path);
        exit(EX_IOERR);
    }
    buffer[bytesRead] = '\0';

    fclose(file);
    return buffer;
}

static void runFile(const char* path) {
    char* source = readFile(path);
    InterpretResult result = interpret(source);
    free(source);

    if (result == INTERPRET_COMPILE_ERROR) {
        exit(EX_DATAERR);
    }

    if (result == INTERPRET_RUNTIME_ERROR) {
        exit(EX_SOFTWARE);
    }
}

int main(int argc, const char* argv[]) {
    setbuf(stdout, NULL);
    setbuf(stderr, NULL);

    initVM();

    if (argc == 1) {
        repl();
    } else if (argc == 2) {
        runFile(argv[1]);
    } else {
        fprintf(stderr, "Usage: clox [path]\n");
        exit(EX_USAGE);
    }

    freeVM();
    return EXIT_SUCCESS;
}

#ifdef __cplusplus
}
#endif
