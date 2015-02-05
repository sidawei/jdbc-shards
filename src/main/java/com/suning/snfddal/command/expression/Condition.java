/*
 * Copyright 2004-2014 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package com.suning.snfddal.command.expression;

import com.suning.snfddal.value.Value;
import com.suning.snfddal.value.ValueBoolean;

/**
 * Represents a condition returning a boolean value, or NULL.
 */
abstract class Condition extends Expression {

    @Override
    public int getType() {
        return Value.BOOLEAN;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public long getPrecision() {
        return ValueBoolean.PRECISION;
    }

    @Override
    public int getDisplaySize() {
        return ValueBoolean.DISPLAY_SIZE;
    }

}