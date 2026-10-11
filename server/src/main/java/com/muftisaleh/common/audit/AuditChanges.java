package com.muftisaleh.common.audit;

import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class AuditChanges {

    static final Set<String> SECRET = Set.of("passwordHash");
    static final Set<String> BOOKKEEPING = Set.of("createdAt", "updatedAt");
    static final String SECRET_MARKER = "«set»";
    static final String SECRET_CLEARED = "«unset»";
    static final int MAX_LENGTH = 300;

    private AuditChanges() {
    }

    public static Map<String, AuditChange> ofInsert(String[] names, String[] values) {
        Map<String, AuditChange> changes = new LinkedHashMap<>();
        for (int i = 0; i < names.length; i++) {
            if (skip(names[i]) || values[i] == null) {
                continue;
            }
            changes.put(names[i], AuditChange.set(mask(names[i], values[i])));
        }
        return changes;
    }

    public static Map<String, AuditChange> ofDelete(String[] names, String[] values) {
        Map<String, AuditChange> changes = new LinkedHashMap<>();
        for (int i = 0; i < names.length; i++) {
            if (skip(names[i]) || values[i] == null) {
                continue;
            }
            changes.put(names[i], AuditChange.cleared(mask(names[i], values[i])));
        }
        return changes;
    }

    public static Map<String, AuditChange> ofUpdate(String[] names, String[] before, String[] after, int[] dirty) {
        Map<String, AuditChange> changes = new LinkedHashMap<>();
        for (int i : dirty == null ? allIndexes(names.length) : dirty) {
            if (i < 0 || i >= names.length || skip(names[i])) {
                continue;
            }
            String from = before == null ? null : before[i];
            String to = after[i];
            if (java.util.Objects.equals(from, to)) {
                continue;
            }
            changes.put(names[i], new AuditChange(mask(names[i], from), mask(names[i], to)));
        }
        return changes;
    }

    public static String render(Object value) {
        if (value == null) {
            return null;
        }
        String text = switch (value) {
            case CharSequence s -> s.toString();
            case Enum<?> e -> e.name();
            case Temporal t -> t.toString();
            case byte[] bytes -> "«%d bytes»".formatted(bytes.length);
            case Collection<?> c -> c.toString();
            case Map<?, ?> m -> m.toString();
            default -> String.valueOf(value);
        };
        return truncate(text);
    }

    private static boolean skip(String name) {
        return BOOKKEEPING.contains(name);
    }

    private static String mask(String name, String value) {
        if (!SECRET.contains(name)) {
            return value;
        }
        return value == null ? SECRET_CLEARED : SECRET_MARKER;
    }

    private static String truncate(String text) {
        return text.length() <= MAX_LENGTH ? text : text.substring(0, MAX_LENGTH) + "…";
    }

    private static int[] allIndexes(int length) {
        int[] all = new int[length];
        Arrays.setAll(all, i -> i);
        return all;
    }
}
