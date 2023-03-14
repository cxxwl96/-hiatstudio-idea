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

import com.cxxwl96.hiatstudio.validate.metadata.ValidationMetadata;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.lang.Assert;

/**
 * ValidateBuilder
 *
 * @author cxxwl96
 * @since 2023/3/3 14:06
 */

public class ValidationBuilder {
    // 参数校验数据元
    private final ValidationMetadata metadata;

    // 方法校验实体
    private final List<MethodValidatorHandler<? extends Annotation>> methodValidators = new ArrayList<>();

    // 参数校验实体
    private final List<ArgumentValidatorHandler<? extends Annotation>> argumentValidators = new ArrayList<>();

    /**
     * 构造器。包内访问，不允许外部创建
     *
     * @param metadata 参数校验数据元
     */
    private ValidationBuilder(ValidationMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * 构造ValidationBuilder
     *
     * @param metadata 参数校验数据元
     * @return ValidationBuilder
     */
    public static ValidationBuilder builder(ValidationMetadata metadata) {
        // 传入的metadata不允许为null
        Assert.notNull(metadata, "metadata cannot be null.");
        return new ValidationBuilder(metadata);
    }

    /**
     * 添加方法校验实体
     *
     * @param validator 方法校验实体
     * @return ValidationBuilder
     */
    public ValidationBuilder addMethodValidator(MethodValidatorHandler<? extends Annotation> validator) {
        // 传入的validator不允许为null
        Assert.notNull(validator, "validator cannot be null.");
        methodValidators.add(validator);
        return this;
    }

    /**
     * 添加参数校验实体
     *
     * @param validator 参数校验实体
     * @return ValidationBuilder
     */
    public ValidationBuilder addArgumentValidator(ArgumentValidatorHandler<? extends Annotation> validator) {
        // 传入的validator不允许为null
        Assert.notNull(validator, "validator cannot be null.");
        argumentValidators.add(validator);
        return this;
    }

    /**
     * 构造参数校验实现逻辑对象
     *
     * @return 参数校验实现逻辑
     */
    public Validation build() {
        return new Validation(metadata, methodValidators, argumentValidators);
    }
}
