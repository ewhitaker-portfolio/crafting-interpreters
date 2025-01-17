#include <stdio.h>
#include <stdlib.h>
#include <string.h>

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
DoublyLinkedNode* makeDoublyLinkedNode(char* value, int length);
void freeDoublyLinkedList(DoublyLinkedList* list);
void insertDoublyLinkedNode(DoublyLinkedList* list, char* value);
void insertDoublyLinkedNodeAt(DoublyLinkedList* list, int i, char* value);

void traverseForward(DoublyLinkedList* list, void (*callback)(DoublyLinkedNode* node));
void traverseBackward(DoublyLinkedList* list, void (*callback)(DoublyLinkedNode* node));

void printNode(DoublyLinkedNode* node) {
    fprintf(stdout, "%s\n", node->value); 
}

int main(void) {
    DoublyLinkedList* list = makeDoublyLinkedList();

    insertDoublyLinkedNode(list, "first value");
    insertDoublyLinkedNode(list, "second value");
    insertDoublyLinkedNode(list, "third value");

    traverseForward(list, printNode);
    traverseBackward(list, printNode);

    freeDoublyLinkedList(list);
}

DoublyLinkedList* makeDoublyLinkedList() {
    DoublyLinkedList* list = (DoublyLinkedList*) malloc(sizeof(DoublyLinkedList));
    list->head = NULL;
    list->tail = NULL;
    list->size = 0;
    return list;
}

DoublyLinkedNode* makeDoublyLinkedNode(char* value, int length) {
    DoublyLinkedNode* node = (DoublyLinkedNode*) malloc(sizeof(DoublyLinkedNode));
    node->prev = NULL;
    node->next = NULL;
    node->value = (char*) malloc(sizeof(char) * length);
    memcpy(node->value, value, length);
    return node;
}

void freeDoublyLinkedList(DoublyLinkedList* list) {
    DoublyLinkedNode* curr = list->head;
    DoublyLinkedNode* prev = NULL;
    while (curr != NULL) {
        free(curr->value);
        prev = curr;
        curr = curr->next;
        free(prev);
    }
}

void insertDoublyLinkedNode(DoublyLinkedList* list, char* value) {
    insertDoublyLinkedNodeAt(list, list->size - 1, value);
}

void insertDoublyLinkedNodeAt(DoublyLinkedList* list, int i, char* value) {
    DoublyLinkedNode* curr = list->head;
    DoublyLinkedNode* node = makeDoublyLinkedNode(
        value, strlen(value)
    );
    if (curr == NULL) {
        list->head = node;
        list->tail = node;
        list->size++;
        return;
    }

    if (i == list->size - 1) {
        curr = list->tail;
    } else {
        int c = 0;
        while (c++ != i) {
            if (curr->next == NULL) {
                break;
            }
            curr = curr->next;
        }
    }

    DoublyLinkedNode* next = curr->next;
    curr->next = node;
    if (next == NULL) {
        list->tail = node;
    } else {
        next->prev = node;
    }
    node->prev = curr;
    node->next = next;
    list->size = list->size + 1;
}

void traverseForward(DoublyLinkedList* list, void (*callback)(DoublyLinkedNode* node)) {
    DoublyLinkedNode* curr = list->head;
    while (curr != NULL) {
        callback(curr);
        curr = curr->next;
    }
}

void traverseBackward(DoublyLinkedList* list, void (*callback)(DoublyLinkedNode* node)) {
    DoublyLinkedNode* curr = list->tail;
    while (curr != NULL) {
        callback(curr);
        curr = curr->prev;
    }
}
