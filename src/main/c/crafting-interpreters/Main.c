#include <assert.h>
#include <stdio.h>

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
    DoublyLinkedList* list = makeDoublyLinkedList();
    traverseDoublyLinkedList(list, printDoublyLinkedNode);

    insertDoublyLinkedNode(list, "first value");
    traverseDoublyLinkedList(list, printDoublyLinkedNode);
    insertDoublyLinkedNode(list, "second value");
    insertDoublyLinkedNode(list, "third value");
    traverseDoublyLinkedList(list, printDoublyLinkedNode);

    assert(indexOfDoublyLinkedNode(list, "second value") == 1);
    assert(indexOfDoublyLinkedNode(list, "fourth value") == -1);

    freeDoublyLinkedList(list);
}

#ifdef __cplusplus
}
#endif
