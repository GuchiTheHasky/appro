package com.appro.entity.project_options;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FoundationOptions {

    STRIP("ленточный"),
    SLAB("плитный"),
    PILE("свайный"),
    COMBINED("комбинированный (свайно-плитный)"),
    COLUMNAR("столбчатый"),
    MONOLITH_STRIP("монолитный ленточный");

    private final String foundation;


    @JsonCreator
    public static FoundationOptions fromValue(String value) {
        for (FoundationOptions option : FoundationOptions.values()) {
            if (option.getFoundation().equalsIgnoreCase(value)) {
                return option;
            }
        }
        throw new IllegalArgumentException("Unknown foundation material: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.foundation;
    }
}
