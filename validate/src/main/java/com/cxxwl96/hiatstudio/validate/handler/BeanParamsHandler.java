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

import com.cxxwl96.hiatstudio.validate.ArgumentValidatorHandler;
import com.cxxwl96.hiatstudio.validate.ValidationChain;
import com.cxxwl96.hiatstudio.validate.annotations.BeanParam;
import com.cxxwl96.hiatstudio.validate.annotations.IgnoreField;
import com.cxxwl96.hiatstudio.validate.annotations.JsonParam;
import com.cxxwl96.hiatstudio.validate.metadata.ElementMetadata;
import com.cxxwl96.hiatstudio.validate.metadata.ValidationMetadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Locale;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ReflectUtil;

/**
 * 处理器：@BeanParams注解校验处理器
 *
 * @author cxxwl96
 * @since 2023/3/3 17:46
 */
public class BeanParamsHandler implements ArgumentValidatorHandler<BeanParam> {
    private BeanParam beanParam;

    /**
     * 初始化方法
     *
     * @param annotation 注解
     */
    @Override
    public void initialize(BeanParam annotation) {
        beanParam = annotation;
    }

    /**
     * 参数校验处理
     *
     * @param metadata 校验元数据
     * @param chain 校验链
     * @param element 方法参数或类字段的元数据
     * @return 校验通过参数的值
     * @throws Exception 参数校验失败异常
     */
    @Override
    public Object handle(ValidationMetadata metadata, ValidationChain chain, ElementMetadata element) throws Exception {
        if (!element.onParameter()) {
            throw new IllegalArgumentException("BeanParam supports only method parameters.");
        }
        final Parameter parameter = element.getParameterOrField(Parameter.class);
        final String paramName = element.getName();
        // 拦截下一个校验处理器
        chain.intercept();
        // 校验个数，配置了参数长度并且不满足个数相等则校验失败
        constraintSize(beanParam.size(), metadata.getParamValues().size());
        // 参数类型是否是标准的JavaBean
        if (!BeanUtil.isBean(parameter.getType())) {
            throw new IllegalArgumentException(
                "The type of parameter \"" + paramName + "\" is not a standard Javabeans");
        }
        // 创建javabean对象并进行字段注入
        Object beanInstance = newBeanInstance(metadata, parameter);
        // 最终通过validate进行校验
        constraintHibernateValidate(beanInstance);
        // 校验通过则返回bean的实例
        return beanInstance;
    }

    /**
     * 创建Bean实例
     *
     * @param metadata metadata
     * @param parameter parameter
     * @return Bean实例
     * @throws Exception exception
     */
    private Object newBeanInstance(ValidationMetadata metadata, Parameter parameter) throws Exception {
        final Object beanInstance = parameter.getType().newInstance();
        int paramValueIndex = 0; // 参数索引
        final Field[] fields = parameter.getType().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            final Field field = fields[i];
            // 过滤静态的字段以及忽略的字段
            if (ModifierUtil.isStatic(field) || field.isAnnotationPresent(IgnoreField.class)) {
                continue;
            }
            Object paramValue;
            // JsonParam注解注入
            if (field.isAnnotationPresent(JsonParam.class)) {
                final JsonParamHandler validator = new JsonParamHandler();
                // 调用初始化方法
                validator.initialize(field.getAnnotation(JsonParam.class));
                // 调用处理器处理方法
                final ElementMetadata element = new ElementMetadata(field, i, field.getName());
                paramValue = validator.handle(metadata, new ValidationChain(), element);
            } else {
                // 校验参数取值是否越界
                constraintIndexOutOfRange(field.getName(), paramValueIndex, metadata.getParamValues().size());
                // 参数值类型转换
                paramValue = typeCast(field.getName(), metadata.getParamValues().get(paramValueIndex++),
                    field.getType());
            }
            // 给bean字段赋值
            final Method setterMethod = findFieldSetterMethod(parameter.getType(), field);
            if (setterMethod != null) {
                // 1、优先调用setter方法调用
                ReflectUtil.invoke(beanInstance, setterMethod, paramValue);
            } else {
                // 2、没有setter方法则直接属性注入
                ReflectUtil.setFieldValue(beanInstance, field, paramValue);
            }
        }
        return beanInstance;
    }

    /**
     * 在class中查找字段的setter方法
     *
     * @param clazz 需要查找的类
     * @param field 需要查找的字段
     * @return 查找到的setter方法
     */
    private Method findFieldSetterMethod(Class<?> clazz, Field field) {
        final String fieldName = field.getName();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase(Locale.ROOT) + fieldName.substring(1);
        try {
            return ReflectUtil.getPublicMethod(clazz, methodName, field.getType());
        } catch (SecurityException exception) {
            return null;
        }
    }
}
