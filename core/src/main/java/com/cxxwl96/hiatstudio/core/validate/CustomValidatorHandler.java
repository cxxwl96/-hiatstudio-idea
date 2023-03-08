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

import java.util.List;

/**
 * 自定义校验接口
 *
 * @author cxxwl96
 * @since 2023/3/8 14:27
 */
public interface CustomValidatorHandler {
    /**
     * 自定义校验处理
     *
     * @param params 入参参数
     * @param chain 校验链
     * @throws IllegalArgumentException 参数校验异常
     */
    void handle(List<String> params, ValidationChain chain) throws IllegalArgumentException;
}
