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
                log.error(exception.getMessage(), exception);
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
                final ValidationChain chain = new ValidationChain();
                validator.handle(metadata, chain);
                // 不执行下一个校验处理器则跳出校验
                if (!chain.doNext()) {
                    break;
                }
            } catch (Exception exception) {
                // 校验抛出异常时终止校验
                throw new ValidationException(exception.getMessage(), exception);
            }
        }
    }

    private Object argumentValidate(int index) {
        final Method runMethod = metadata.getRunMethod();
        final Parameter parameter = runMethod.getParameters()[index];
        final String paramName = metadata.getParamNames()[index];
        Object paramValue = null; // 参数真实类型的值
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
                // 若参数上没有ValidatorHandler填充的校验注解，则跳过
                if (!parameter.isAnnotationPresent(validAnnotation)) {
                    continue;
                }
                // 若参数上有ValidatorHandler填充的校验注解，则调用校验注解处理器的实现
                // 这里使用校验链的目的是因为一个参数可能被多个校验处理器处理
                // 多个校验处理器处理的时候返回的是最后一个处理器处理的结果，除非处理器自身调用校验链的拦截方法
                final ValidationChain chain = new ValidationChain();
                paramValue = validator.handle(metadata, chain, parameter, index, paramName);
                // 不执行下一个校验处理器则直接返回参数真实类型的参数值
                if (!chain.doNext()) {
                    return paramValue;
                }
            } catch (Exception exception) {
                // 校验抛出异常时终止校验
                throw new ValidationException(exception.getMessage(), exception);
            }
        }
        // 若上面没有任何一个校验处理器拦截的话，这里返回最后一个校验处理器处理的结果
        if (paramValue != null) {
            return paramValue;
        }
        // 若都不满足条件则返回默认值
        return ClassUtil.getDefaultValue(parameter.getType());
    }

}
