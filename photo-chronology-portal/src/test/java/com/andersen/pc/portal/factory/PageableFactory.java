package com.andersen.pc.portal.factory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageableFactory {

    private static final Integer PAGE_SIZE = 10;
    private static final Integer PAGE_NUMBER = 1;

    public static Pageable getPageableObject() {
        return PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    }
}
