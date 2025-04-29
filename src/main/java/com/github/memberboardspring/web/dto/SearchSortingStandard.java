package com.github.memberboardspring.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum SearchSortingStandard {
    TITLE,
    LATEST,
    LIKES,
    VIEWS,
    COMMENTS;


    private boolean isDescending;
    public void setDescending(boolean isDescending) {
        this.isDescending = isDescending;
    }
}
