#include <assert.h>
#include <stdbool.h>
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
DoublyLinkedNode* makeDoublyLinkedNode(const char* value, int length);
void freeDoublyLinkedList(DoublyLinkedList* list);
void insertDoublyLinkedNode(DoublyLinkedList* list, const char* value);
void insertDoublyLinkedNodeAt(DoublyLinkedList* list, int i, const char* value);
int indexOfDoublyLinkedNode(DoublyLinkedList* list, const char* value);

void traverseDoublyLinkedList(DoublyLinkedList* list, void (*callback)(DoublyLinkedNode* node));
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

DoublyLinkedList* makeDoublyLinkedList() {
    DoublyLinkedList* list = (DoublyLinkedList*) malloc(sizeof(DoublyLinkedList));
    list->head = NULL;
    list->tail = NULL;
    list->size = 0;
    return list;
}

DoublyLinkedNode* makeDoublyLinkedNode(const char* value, int length) {
    assert(value != NULL);

    DoublyLinkedNode* node = (DoublyLinkedNode*) malloc(sizeof(DoublyLinkedNode));
    node->prev = NULL;
    node->next = NULL;
    node->value = (char*) malloc(sizeof(char) * length);
    memcpy(node->value, value, length);
    return node;
}

void freeDoublyLinkedList(DoublyLinkedList* list) {
    assert(list != NULL);

    DoublyLinkedNode* curr = list->head;
    DoublyLinkedNode* prev = NULL;
    while (curr != NULL) {
        free(curr->value);
        prev = curr;
        curr = curr->next;
        free(prev);
    }
}

void insertDoublyLinkedNode(DoublyLinkedList* list, const char* value) {
    insertDoublyLinkedNodeAt(list, list->size, value);
}

void insertDoublyLinkedNodeAt(DoublyLinkedList* list, int index, const char* value) {
    assert(list != NULL && index >= 0 && index <= list->size && value != NULL);

    DoublyLinkedNode* node = makeDoublyLinkedNode(
        value, strlen(value)
    );

    DoublyLinkedNode* curr;
    if (index == list->size) {
        curr = list->tail;
        if (curr == NULL) {
            list->head = node;
        } else {
            curr->next = node;
        }
        list->tail = node;
        node->prev = curr;
        node->next = NULL;
    } else {
        curr = list->tail;
        for (int i = list->size; i > index; --i) {
            curr = curr->prev;
        }

        DoublyLinkedNode* prev = curr->prev;
        curr->prev = node;
        if (prev == NULL) {
            list->head = node;
        } else {
            prev->prev = node;
        }
        node->prev = prev;
        node->next = curr;
    }

    list->size = list->size + 1;
}

bool equals(const char* a, const char* b) {
    assert(a != NULL && b != NULL);

    int len = strlen(a);
    if (len != strlen(b)) {
        return false;
    }
    return strncmp(a, b, len) == 0;
}

int indexOfDoublyLinkedNode(DoublyLinkedList* list, const char* value) {
    assert(list != NULL && value != NULL);

    DoublyLinkedNode* curr = list->head;
    int i = 0;
    while (curr != NULL && i < list->size) {
        if (equals(curr->value, value)) {
            break;
        }
        i = i + 1;
        curr = curr->next;
    }
    return curr == NULL ? -1 : i;
}

void traverseDoublyLinkedList(DoublyLinkedList* list, void (*callback)(DoublyLinkedNode* node)) {
    DoublyLinkedNode* curr = list->head;
    while (curr != NULL) {
        callback(curr);
        curr = curr->next;
    }
}
