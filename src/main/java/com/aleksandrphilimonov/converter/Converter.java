package com.aleksandrphilimonov.converter;

public interface Converter<S, T> {
    T convert(S source);
}
