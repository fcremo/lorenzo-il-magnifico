package gamecontroller.utils;

import java.util.stream.Stream;

public class StreamUtils {
    public static <T> Stream<T> takeRandomElements(Stream<T> stream, int limit) {
        return stream.collect(CollectorUtils.toShuffledStream())
                     .limit(limit);
    }
}
