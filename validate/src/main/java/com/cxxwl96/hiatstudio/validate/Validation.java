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

import com.cxxwl96.hiatstudio.validate.metadata.ElementMetadata;
import com.cxxwl96.hiatstudio.validate.metadata.ValidationMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

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
    private final List<MethodValidatorHandler<? extends Annotation>> methodValidators;

    // 参数校验实体
    private final List<ArgumentValidatorHandler<? extends Annotation>> argumentValidators;

    /**
     * 构造器。包内访问，不允许外部创建
     *
     * @param metadata 参数校验数据元
     * @param methodValidators 方法校验实体
     * @param argumentValidators 参数校验实体
     */
    Validation(ValidationMetadata metadata, List<MethodValidatorHandler<? extends Annotation>> methodValidators,
        List<ArgumentValidatorHandler<? extends Annotation>> argumentValidators) {
        this.metadata = metadata;
        this.methodValidators = methodValidators;
        this.argumentValidators = argumentValidators;
    }

    /**
     * 参数校验逻辑
     *
     * @return 校验结果
     */
    public ValidationResult validate() {
        // 无校验处理器时默认校验结果成功，且返回结果中无参数列表的值
        if (methodValidators.size() == 0 && argumentValidators.size() == 0) {
            return ValidationResult.success();
        }
        List<Object> paramValues = new ArrayList<>(); // 功能接口参数列表值
        try {
            // 校验方法实体
            methodValidate();
            // 校验参数实体
            for (int index = 0; index < metadata.getRunMethod().getParameters().length; index++) {
                // 执行校验实体校验参数
                paramValues.add(argumentValidate(index));
            }
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return ValidationResult.failed().setErrorMessage(exception.getMessage());
        }
        // 返回校验结果
        return ValidationResult.success().setParamValues(paramValues.toArray());
    }

    private void methodValidate() throws Exception {
        for (MethodValidatorHandler<? extends Annotation> validator : methodValidators) {
            // 1、调用初始化方法
            if (!invokeInitializable(validator, metadata.getRunMethod())) {
                // 方法或方法参数上不包含此校验处理器的校验注解，则跳过此校验处理器
                continue;
            }
            // 2、调用处理器处理方法，这里使用校验链的目的是因为一个方法可能被多个校验处理器处理，多个校验处理器处理的时候返回的是最后一个处理器处理的结果，除非处理器自身调用校验链的拦截方法
            final ValidationChain chain = new ValidationChain();
            validator.handle(metadata, chain);
            // 处理完成后判断校验链是否不执行下一个校验处理器，不执行则退出校验，后面的校验处理器则不会执行
            if (!chain.doNext()) {
                break;
            }
        }
    }

    private Object argumentValidate(int index) throws Exception {
        final Parameter parameter = metadata.getRunMethod().getParameters()[index];
        final String paramName = metadata.getParamNames().get(index);
        Object paramValue = null; // 参数真实类型的值
        for (ArgumentValidatorHandler<? extends Annotation> validator : argumentValidators) {
            // 1、调用初始化方法
            if (!invokeInitializable(validator, parameter)) {
                // 方法或方法参数上不包含此校验处理器的校验注解，则跳过此校验处理器
                continue;
            }
            // 2、调用处理器处理方法，这里使用校验链的目的是因为一个参数可能被多个校验处理器处理，多个校验处理器处理的时候返回的是最后一个处理器处理的结果，除非处理器自身调用校验链的拦截方法
            final ValidationChain chain = new ValidationChain();
            final ElementMetadata element = new ElementMetadata(parameter, index, paramName); // 默认传入的是方法参数的元数据
            paramValue = validator.handle(metadata, chain, element);
            // 处理完成后判断校验链是否不执行下一个校验处理器，不执行则直接返回参数真实类型的参数值
            if (!chain.doNext()) {
                return paramValue;
            }
        }
        // 若上面没有任何一个校验处理器拦截的话，这里返回最后一个校验处理器处理的结果
        if (paramValue != null) {
            return paramValue;
        }
        // 若都不满足条件则返回默认值
        return ClassUtil.getDefaultValue(parameter.getType());
    }

    /**
     * 调用校验处理器的初始化方法
     *
     * @param validator 校验处理器
     * @param annotatedElement 方法或参数
     * @return 方法或方法参数上是否包含此校验处理器的校验注解
     */
    private boolean invokeInitializable(Initializable<?> validator, AnnotatedElement annotatedElement) {
        // 1、获取校验处理器接口泛型，即校验注解
        // 获取校验处理器实现的接口
        final Type[] genericInterfaces = validator.getClass().getGenericInterfaces();
        // 校验处理器实现的接口中就一个接口是ArgumentValidatorHandler
        Class<? extends Annotation> validAnnotationClass = null; // 校验注解
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedTypeImpl) {
                // 获取校验处理器实现的接口
                final Type[] arguments = ((ParameterizedTypeImpl) genericInterface).getActualTypeArguments();
                // 因为校验处理器接口泛型列表就只有一个，所以直接返回
                if (arguments.length == 1) {
                    validAnnotationClass = (Class<? extends Annotation>) arguments[0];
                    break;
                }
            }
        }
        // 若方法或方法参数上没有此校验处理器的校验注解，则跳过此校验处理器
        if (validAnnotationClass == null || !annotatedElement.isAnnotationPresent(validAnnotationClass)) {
            return false;
        }
        // 2、调用校验处理器的初始化方法
        // 若方法或方法参数上有此校验处理器的校验注解，则调用校验注解处理器的实现
        // 则调用校验处理器的初始化方法，不直接使用validator.initialize()调用的原因是因为Java有泛型擦除，语法检查上不支持
        final Method initializeMethod = ReflectUtil.getMethodByName(validator.getClass(), "initialize");
        if (initializeMethod != null) {
            ReflectUtil.invoke(validator, initializeMethod, annotatedElement.getAnnotation(validAnnotationClass));
        }
        return true;
    }
}
