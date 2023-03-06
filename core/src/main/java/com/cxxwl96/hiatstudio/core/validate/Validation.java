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

import com.cxxwl96.hiatstudio.core.validate.annotations.ValidatorHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ValidationException;

import cn.hutool.core.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 参数校验实现逻辑
 *
 * @author cxxwl96
 * @since 2023/3/3 14:36
 */
@Slf4j
public class Validation {
    // 参数校验数据元
    private final ValidationMetadata metadata;

    // 方法校验实体
    private final List<MethodValidatorHandler> methodValidators;

    // 参数校验实体
    private final List<ArgumentValidatorHandler> argumentValidators;

    /**
     * 构造器。包内访问，不允许外部创建
     *
     * @param metadata 参数校验数据元
     * @param methodValidators 方法校验实体
     * @param argumentValidators 参数校验实体
     */
    Validation(ValidationMetadata metadata, List<MethodValidatorHandler> methodValidators,
        List<ArgumentValidatorHandler> argumentValidators) {
        this.metadata = metadata;
        this.methodValidators = methodValidators;
        this.argumentValidators = argumentValidators;
    }

    /**
     * 参数校验逻辑
     *
     * @return 校验结果
     * @throws ValidationException 校验实体可能产生的异常，通过包装成校验失败异常
     */
    public ValidationResult validate() throws ValidationException {
        // 无校验实体时默认校验结果成功，且无参数列表的值
        if (methodValidators.size() == 0 && argumentValidators.size() == 0) {
            return ValidationResult.success();
        }
        // 校验方法实体
        methodValidate();
        // 校验参数实体
        List<Object> paramValues = new ArrayList<>(); // 功能接口参数列表值
        for (int index = 0; index < metadata.getRunMethod().getParameters().length; index++) {
            try {
                // 执行校验实体校验参数
                paramValues.add(argumentValidate(index));
            } catch (Exception exception) {
                return ValidationResult.failed().setErrorMessage(exception.getMessage());
            }
        }
        // 返回校验结果
        return ValidationResult.success().setParamValues(paramValues.toArray());
    }

    private void methodValidate() {
        for (MethodValidatorHandler validator : methodValidators) {
            try {
                // 校验处理器没有被ValidatorHandler注解修饰则不调用校验处理器的实现
                if (!validator.getClass().isAnnotationPresent(ValidatorHandler.class)) {
                    continue;
                }
                // 获取需要被校验的注解
                final Class<? extends Annotation> validAnnotation = validator.getClass()
                    .getAnnotation(ValidatorHandler.class)
                    .annotation();
                // 执行的方法上不包含需要被校验的注解则不调用校验注解处理器的实现
                if (!metadata.getRunMethod().isAnnotationPresent(validAnnotation)) {
                    continue;
                }
                // 执行校验逻辑
                validator.handle(metadata);
            } catch (Exception exception) {
                // 校验抛出异常时终止校验
                log.error(exception.getMessage());
                throw new ValidationException(exception.getMessage(), exception);
            }
        }
    }

    private Object argumentValidate(int index) {
        final Method runMethod = metadata.getRunMethod();
        final Parameter parameter = runMethod.getParameters()[index];
        final String paramName = metadata.getParamNames()[index];
        for (ArgumentValidatorHandler validator : argumentValidators) {
            try {
                // 校验处理器没有被ValidatorHandler注解修饰则不调用校验处理器的实现
                if (!validator.getClass().isAnnotationPresent(ValidatorHandler.class)) {
                    continue;
                }
                // 获取需要被校验的注解
                final Class<? extends Annotation> validAnnotation = validator.getClass()
                    .getAnnotation(ValidatorHandler.class)
                    .annotation();
                // 若参数上有ValidatorHandler填充的校验注解，则调用校验注解处理器的实现
                if (parameter.isAnnotationPresent(validAnnotation)) {
                    // 满足校验条件则执行校验逻辑，并返回参数真实类型的参数值
                    return validator.handle(metadata, parameter, index, paramName);
                }
            } catch (Exception exception) {
                // 校验抛出异常时终止校验
                log.error(exception.getMessage());
                throw new ValidationException(exception.getMessage(), exception);
            }
        }
        // 都不满足条件则返回默认值
        return ClassUtil.getDefaultValue(parameter.getType());
    }

}
