package io.github.recrivenvi.mobends.lib.flux;

@FunctionalInterface
public interface IComputedExpression<T> {

    T compute();
}
