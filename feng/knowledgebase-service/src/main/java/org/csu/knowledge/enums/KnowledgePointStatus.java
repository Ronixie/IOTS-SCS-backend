package org.csu.knowledge.enums;

import lombok.Getter;

@Getter
public enum KnowledgePointStatus {
    DRAFT("draft"),
    PUBLISHED("published");
    final String value;

    KnowledgePointStatus(String value) {
        this.value = value;
    }
}
