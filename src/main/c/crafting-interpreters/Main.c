#include <assert.h>
#include <stdio.h>
#include <stdlib.h>

#include "DoublyLinkedList.h"

#ifdef __cplusplus
extern "C" {
#endif

void printDoublyLinkedNode(DoublyLinkedNode* node) {
    if (node->next == NULL) {
        fprintf(stdout, "%s\n", node->value);
    } else {
        fprintf(stdout, "%s <-> ", node->value);
    }
}

int main(void) {
    setbuf(stdout, NULL);
    setbuf(stderr, NULL);

    DoublyLinkedList* list = makeDoublyLinkedList();
    traverseDoublyLinkedList(list, printDoublyLinkedNode);

    insertDoublyLinkedNode(list, "first value");
    traverseDoublyLinkedList(list, printDoublyLinkedNode);
    insertDoublyLinkedNode(list, "second value");
    insertDoublyLinkedNode(list, "third value");
    traverseDoublyLinkedList(list, printDoublyLinkedNode);

    assert(indexOfDoublyLinkedNode(list, "second value") == 1);
    assert(indexOfDoublyLinkedNode(list, "fourth value") == -1);

    assert(removeDoublyLinkedNode(list, "second value") != NULL);
    traverseDoublyLinkedList(list, printDoublyLinkedNode);
    assert(removeDoublyLinkedNode(list, "first value") != NULL);
    assert(removeDoublyLinkedNode(list, "fifth value") == NULL);
    traverseDoublyLinkedList(list, printDoublyLinkedNode);

    insertDoublyLinkedNode(list, "fourth value");
    insertDoublyLinkedNode(list, "fifth value");
    traverseDoublyLinkedList(list, printDoublyLinkedNode);
    assert(removeDoublyLinkedNode(list, "fifth value") != NULL);
    traverseDoublyLinkedList(list, printDoublyLinkedNode);

    freeDoublyLinkedList(list);

    return EXIT_SUCCESS;
}

#ifdef __cplusplus
}
#endif
