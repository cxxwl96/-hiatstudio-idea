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

package com.cxxwl96.hiatstudio.core.validate;

/**
 * 校验链
 *
 * @author cxxwl96
 * @since 2023/3/8 13:34
 */
public class ValidationChain {
    // 是否执行下一个校验处理器。默认需要执行
    private boolean doNext = true;

    /**
     * 拦截不执行下一个校验处理器
     */
    public void intercept() {
        doNext = false;
    }

    /**
     * 是否执行下一个校验处理器
     *
     * @return 是否执行下一个校验处理器
     */
    public boolean doNext() {
        return doNext;
    }

}
