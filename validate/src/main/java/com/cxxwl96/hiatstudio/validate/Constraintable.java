/*
 * Copyright (c) 2021-2023, jad (cxxwl96@sina.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cxxwl96.hiatstudio.validate;

import java.util.Locale;

/**
 * 参数约束接口
 *
 * @author cxxwl96
 * @since 2023/3/13 18:15
 */
public interface Constraintable {
    /**
     * 参数长度约束，仅配置了参数长度才进行校验
     *
     * @param expectedSize 期望的长度
     * @param actualSize 实际长度
     */
    default void constraintSize(int expectedSize, int actualSize) {
        // 校验个数，配置了参数长度并且不满足个数相等则校验失败
        if (expectedSize >= 0 && expectedSize != actualSize) {
            final String error = String.format(Locale.ROOT,
                "The number of parameters is not equal. %d parameters are expected, but %d parameters are obtained.",
                expectedSize, actualSize);
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * 参数索引约束，不允许超过实际长度
     *
     * @param paramName 参数名
     * @param index 参数索引
     * @param actualSize 实际长度
     */
    default void constraintIndexOutOfRange(String paramName, int index, int actualSize) {
        if (index < 0 || index >= actualSize) {
            final String error = String.format(Locale.ROOT,
                "Out of range. There are only %d input parameters, but \"%s\" takes a %d parameter.", index, paramName,
                index + 1);
            throw new IndexOutOfBoundsException(error);
        }
    }
}
