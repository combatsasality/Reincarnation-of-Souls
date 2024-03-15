package com.combatsasality.scol.items.generic;

public interface ISoulMaterial {
    enum SoulType {
        AGGRESSIVE,
        FRIENDLY,
        NEGATIVE
    }

    SoulType getSoulType();
}
