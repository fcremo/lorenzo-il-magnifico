package model.util;

import java.io.Serializable;

public class Tuple<T1, T2> implements Serializable {
    public T1 first;
    public T2 second;

    public Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
}
