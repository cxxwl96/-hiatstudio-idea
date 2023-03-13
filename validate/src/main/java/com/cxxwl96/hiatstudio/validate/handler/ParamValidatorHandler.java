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

package com.cxxwl96.hiatstudio.validate.handler;

import com.cxxwl96.hiatstudio.validate.CustomValidatorHandler;
import com.cxxwl96.hiatstudio.validate.MethodValidatorHandler;
import com.cxxwl96.hiatstudio.validate.ValidationChain;
import com.cxxwl96.hiatstudio.validate.ValidationMetadata;
import com.cxxwl96.hiatstudio.validate.annotations.ParamValidator;

import lombok.SneakyThrows;

/**
 * 处理器：@ParamValidator注解校验处理器
 *
 * @author cxxwl96
 * @since 2023/3/3 15:38
 */
public class ParamValidatorHandler implements MethodValidatorHandler<ParamValidator> {
    private ParamValidator paramValidator;

    /**
     * 初始化方法
     *
     * @param annotation 注解
     */
    @Override
    public void initialize(ParamValidator annotation) {
        paramValidator = annotation;
    }

    /**
     * 方法校验处理
     *
     * @param metadata 校验元数据
     * @param chain 校验链
     */
    @Override
    @SneakyThrows
    public void handle(ValidationMetadata metadata, ValidationChain chain) {
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
        // 校验个数，配置了参数长度并且不满足个数相等则校验失败
        constraintSize(paramValidator.size(), metadata.getParamValues().size());
    }
}
