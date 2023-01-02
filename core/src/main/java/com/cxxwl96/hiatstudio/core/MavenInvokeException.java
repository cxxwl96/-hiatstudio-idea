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

package com.cxxwl96.hiatstudio.core;

/**
 * MavenInvokeException
 *
 * @author cxxwl96
 * @since 2023/1/1 15:13
 */
public class MavenInvokeException extends RuntimeException {
    public MavenInvokeException() {
    }

    public MavenInvokeException(String message) {
        super(message);
    }

    public MavenInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MavenInvokeException(Throwable cause) {
        super(cause);
    }

    public MavenInvokeException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
