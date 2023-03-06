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

package com.cxxwl96.hiatstudio.core.validate.handler;

import com.cxxwl96.hiatstudio.core.validate.MethodValidatorHandler;
import com.cxxwl96.hiatstudio.core.validate.ValidationMetadata;
import com.cxxwl96.hiatstudio.core.validate.annotations.ParamValidator;
import com.cxxwl96.hiatstudio.core.validate.annotations.ValidatorHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

/**
 * 处理器：@ParamValidator注解校验处理器
 *
 * @author cxxwl96
 * @since 2023/3/3 15:38
 */
@ValidatorHandler(annotation = ParamValidator.class)
public class ParamValidatorHandler implements MethodValidatorHandler {
    /**
     * 方法校验处理
     *
     * @param metadata 校验元数据
     */
    @Override
    public void handle(ValidationMetadata metadata) {
        final Method runMethod = metadata.getRunMethod();
        final List<String> paramValues = metadata.getParamValues();
        // 拥有ParamValidator注解则优先校验参数个数
        if (runMethod.getAnnotation(ParamValidator.class).valid()) {
            final int expectedSize = runMethod.getAnnotation(ParamValidator.class).size();
            // 不满足个数相等则校验失败
            if (expectedSize != paramValues.size()) {
                final String error = String.format(Locale.ROOT,
                    "The number of parameters is not equal. %d parameters are expected, but %d parameters are obtained.",
                    expectedSize, paramValues.size());
                throw new IllegalArgumentException(error);
            }
        }
    }
}
