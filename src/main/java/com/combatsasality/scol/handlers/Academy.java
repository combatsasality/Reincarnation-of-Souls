package com.combatsasality.scol.handlers;

public enum Academy {
    NULL((byte) 0),
    LIGHTING_ROD_HAWK((byte) 1),
    UNITY((byte) 2),
    EARTHLY_SILENCE((byte) 3),
    HERMIT((byte) 4),
    EXILED_WANDERER((byte) 5),
    FIRE_BASILISK((byte) 6),
    DEATH((byte) 7),
    PACIFYING_FLOW((byte) 8),
    NATURE_KEEPER((byte) 9);

    private final byte id;

    Academy(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    public static Academy getFromId(byte id) {
        for (Academy e : Academy.values()) {
            if (e.getId() == id) {
                return e;
            }
        }
        return NULL;
    }
}
