package gamecontroller.utils;

import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectorUtils {
    /**
     * Collects a stream and shuffles the elements
     *
     * Code snippet taken from https://stackoverflow.com/a/35512753
     *
     * @param <T>
     * @return
     */
    public static <T> Collector<T, ?, Stream<T>> toShuffledStream() {
        return Collectors.collectingAndThen(Collectors.toList(), collected -> {
            Collections.shuffle(collected);
            return collected.stream();
        });
    }
}
