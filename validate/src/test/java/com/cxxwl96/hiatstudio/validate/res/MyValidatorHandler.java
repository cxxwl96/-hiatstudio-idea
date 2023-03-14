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

package com.cxxwl96.hiatstudio.validate.res;

import com.cxxwl96.hiatstudio.validate.CustomValidatorHandler;
import com.cxxwl96.hiatstudio.validate.ValidationChain;

import java.util.List;

/**
 * MyValidatorHandler
 *
 * @author cxxwl96
 * @since 2023/3/14 19:04
 */
public class MyValidatorHandler implements CustomValidatorHandler {
    /**
     * 自定义校验处理
     *
     * @param paramValues 入参参数
     * @param chain 校验链
     * @throws IllegalArgumentException 参数校验异常
     */
    @Override
    public void handle(List<String> paramValues, ValidationChain chain) throws IllegalArgumentException {
        // 设置校验链拦截，拦截后则不校验@ParamValidator(size = 3)设置的size=3的长度
        chain.intercept();
        if (paramValues.size() != 3) {
            throw new IllegalArgumentException("The number of arguments is not equal to 3.");
        }
    }
}