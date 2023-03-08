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

import com.cxxwl96.hiatstudio.core.validate.CustomValidatorHandler;
import com.cxxwl96.hiatstudio.core.validate.MethodValidatorHandler;
import com.cxxwl96.hiatstudio.core.validate.ValidationChain;
import com.cxxwl96.hiatstudio.core.validate.ValidationMetadata;
import com.cxxwl96.hiatstudio.core.validate.annotations.ParamValidator;
import com.cxxwl96.hiatstudio.core.validate.annotations.ValidatorHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import lombok.SneakyThrows;

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
     * @param chain 校验链
     */
    @Override
    @SneakyThrows
    public void handle(ValidationMetadata metadata, ValidationChain chain) {
        final Method runMethod = metadata.getRunMethod();
        final List<String> paramValues = metadata.getParamValues();
        final ParamValidator paramValidator = runMethod.getAnnotation(ParamValidator.class);
        // 是否设置了自定义校验，设置了则优先自定义校验
        final Class<? extends CustomValidatorHandler>[] classes = paramValidator.customValidatorHandler();
        for (Class<? extends CustomValidatorHandler> clazz : classes) {
            if (!clazz.isInterface()) {
                clazz.newInstance().handle(metadata.getParamValues(), chain);
            }
            if (!chain.doNext()) {
                return;
            }
        }
        // 校验个数，不满足个数相等则校验失败
        final int expectedSize = paramValidator.size();
        if (expectedSize != paramValues.size()) {
            final String error = String.format(Locale.ROOT,
                "The number of parameters is not equal. %d parameters are expected, but %d parameters are obtained.",
                expectedSize, paramValues.size());
            throw new IllegalArgumentException(error);
        }
    }
}
