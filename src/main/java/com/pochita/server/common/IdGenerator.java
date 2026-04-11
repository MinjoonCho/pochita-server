package com.pochita.server.common;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static String newId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String newInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}
