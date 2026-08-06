package com.pkos.backend.util;

public final class VectorUtils {

    private VectorUtils() {
    }

    public static String toPgVector(float[] embedding) {

        if (embedding == null || embedding.length == 0) {
            throw new IllegalArgumentException(
                    "Embedding cannot be null or empty."
            );
        }

        StringBuilder builder = new StringBuilder();

        builder.append('[');

        for (int i = 0; i < embedding.length; i++) {

            if (i > 0) {
                builder.append(',');
            }

            builder.append(embedding[i]);
        }

        builder.append(']');

        return builder.toString();
    }

}