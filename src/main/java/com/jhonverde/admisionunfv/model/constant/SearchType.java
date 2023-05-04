package com.jhonverde.admisionunfv.model.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum SearchType {

    CODE(1),
    DNI(2);

    private int searchCode;

    public static Optional<SearchType> getBySearchName(String searchName) {
        return Arrays.asList(SearchType.values()).stream()
                .filter(value -> value.name().equals(searchName))
                .findFirst();
    }
}
