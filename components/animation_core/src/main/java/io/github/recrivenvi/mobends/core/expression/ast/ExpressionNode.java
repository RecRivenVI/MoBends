package io.github.recrivenvi.mobends.core.expression.ast;

import io.github.recrivenvi.mobends.core.expression.ExpressionContext;

/** Base interface for all AST nodes in the expression tree. */
public interface ExpressionNode {
    /** Evaluates this node and returns the result. */
    double evaluate(ExpressionContext context);

    /** Returns true if this node is a constant that can be evaluated at compile time. */
    default boolean isConstant() {
        return false;
    }

    /** Attempts to fold constants in this node, returning an optimized node. */
    default ExpressionNode optimize() {
        return this;
    }
}
