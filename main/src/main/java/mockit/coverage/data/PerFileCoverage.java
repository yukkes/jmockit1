/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.coverage.data;

import java.io.Serializable;

import javax.annotation.Nonnegative;

public interface PerFileCoverage extends Serializable {
    @Nonnegative
    int getTotalItems();

    @Nonnegative
    int getCoveredItems();

    int getCoveragePercentage();
}
