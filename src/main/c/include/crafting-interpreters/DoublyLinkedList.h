#ifndef CRAFTING_INTERPRETERS_DOUBLY_LINKED_LIST_H
#define CRAFTING_INTERPRETERS_DOUBLY_LINKED_LIST_H

#ifdef __cplusplus
extern "C" {
#endif

struct DoublyLinkedNode {
    struct DoublyLinkedNode* prev;
    struct DoublyLinkedNode* next;
    char* value;
};

typedef struct DoublyLinkedNode DoublyLinkedNode;

typedef struct {
    DoublyLinkedNode* head;
    DoublyLinkedNode* tail;
    int size;
} DoublyLinkedList;

DoublyLinkedList* makeDoublyLinkedList();
DoublyLinkedNode* makeDoublyLinkedNode(const char* value, int length);
void freeDoublyLinkedList(DoublyLinkedList* list);

void insertDoublyLinkedNode(DoublyLinkedList* list, const char* value);
void insertDoublyLinkedNodeAt(DoublyLinkedList* list, int i, const char* value);

int indexOfDoublyLinkedNode(DoublyLinkedList* list, const char* value);

void traverseDoublyLinkedList(DoublyLinkedList* list, void (*callback)(DoublyLinkedNode* node));

#ifdef __cplusplus
}
#endif

#endif