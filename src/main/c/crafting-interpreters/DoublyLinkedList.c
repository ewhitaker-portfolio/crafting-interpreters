#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "DoublyLinkedList.h"

#ifdef __cplusplus
extern "C" {
#endif

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
    char* chars = (char*) malloc(sizeof(char) * length);
    strncpy(chars, value, length);
    node->value = chars;
    return node;
}

void freeDoublyLinkedList(DoublyLinkedList* list) {
    assert(list != NULL);

    DoublyLinkedNode* curr = list->head;
    DoublyLinkedNode* prev = NULL;
    while (curr != NULL) {
        free((void*) curr->value);
        prev = curr;
        curr = curr->next;
        free(prev);
    }
}

static DoublyLinkedNode* findDoublyLinkedNode(DoublyLinkedList* list, int index) {
    DoublyLinkedNode* curr;
    if (index < (list->size >> 1)) {
        curr = list->head;
        for (int i = 0; i < index; ++i) {
            curr = curr->next;
        }
    } else {
        curr = list->tail;
        for (int i = list->size - 1; i > index; --i) {
            curr = curr->prev;
        }
    }
    return curr;
}

void insertDoublyLinkedNode(DoublyLinkedList* list, const char* value) {
    insertDoublyLinkedNodeAt(list, list->size, value);
}

void insertDoublyLinkedNodeAt(DoublyLinkedList* list, int index, const char* value) {
    assert(list != NULL && index >= 0 && index <= list->size && value != NULL);

    DoublyLinkedNode* node = makeDoublyLinkedNode(value, strlen(value));

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
        curr = findDoublyLinkedNode(list, index);
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

static bool equals(const char* a, const char* b) {
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

const char* removeDoublyLinkedNode(DoublyLinkedList* list, const char* value) {
    return removeDoublyLinkedNodeAt(list, indexOfDoublyLinkedNode(list, value));
}

const char* removeDoublyLinkedNodeAt(DoublyLinkedList* list, int index) {
    assert(list != NULL && index >= -1 && index < list->size);

    if (index == -1) {
        return NULL;
    }

    DoublyLinkedNode* curr = findDoublyLinkedNode(list, index);
    const char* value = curr->value;

    if (curr->prev == NULL) {
        list->head = curr->next;
    } else {
        curr->prev->next = curr->next;
    }

    if (curr->next == NULL) {
        list->tail = curr->prev;
    } else {
        curr->next->prev = curr->prev;
    }

    free(curr);
    return value; 
}

void traverseDoublyLinkedList(DoublyLinkedList* list, void (*callback)(DoublyLinkedNode* node)) {
    DoublyLinkedNode* curr = list->head;
    while (curr != NULL) {
        callback(curr);
        curr = curr->next;
    }
}

#ifdef __cplusplus
}
#endif
