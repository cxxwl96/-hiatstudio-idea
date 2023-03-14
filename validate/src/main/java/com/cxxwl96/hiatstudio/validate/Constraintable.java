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

import com.cxxwl96.hiatstudio.validate.utils.ValidationUtil;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.validation.BeanValidationResult;

/**
 * 参数约束接口
 *
 * @author cxxwl96
 * @since 2023/3/13 18:15
 */
public interface Constraintable {
    /**
     * 参数长度约束，仅配置了参数长度才进行校验
     *
     * @param expectedSize 期望的长度
     * @param actualSize 实际长度
     */
    default void constraintSize(int expectedSize, int actualSize) {
        // 校验个数，配置了参数长度并且不满足个数相等则校验失败
        if (expectedSize >= 0 && expectedSize != actualSize) {
            final String error = String.format(Locale.ROOT,
                "The number of parameters is not equal. %d parameters are expected, but %d parameters are obtained.",
                expectedSize, actualSize);
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * 参数索引约束，不允许超过实际长度
     *
     * @param paramName 参数名
     * @param index 参数索引
     * @param actualSize 实际长度
     */
    default void constraintIndexOutOfRange(String paramName, int index, int actualSize) {
        if (index < 0 || index >= actualSize) {
            final String error = String.format(Locale.ROOT,
                "Out of range. There are only %d input parameters, but \"%s\" takes a %d parameter.", index, paramName,
                index + 1);
            throw new IndexOutOfBoundsException(error);
        }
    }

    /**
     * 校验方法参数上的hibernate-validator的校验注解
     *
     * @param parameter 方法参数
     */
    default void constraintHibernateValidationAnnotations(Parameter parameter, String paramName, Object paramValue)
        throws InstantiationException, IllegalAccessException {
        // 使用字节码增强动态生成bean对象，将方法参数上的hibernate-validator的校验注解和对应的方法参数动态生成javabean
        // 最后通过validate校验
        DynamicType.Builder<Object> dynamicBean = new ByteBuddy().subclass(Object.class).name("HibernateValidateBean");
        // 过滤得到可以放置在类属性上的注解
        List<Annotation> validAnnoList = Arrays.stream(parameter.getAnnotations()).filter(annotation -> {
            // 获取参数上注解类的Target注解
            return Arrays.stream(annotation.annotationType().getAnnotation(Target.class).value())
                .anyMatch(elementType -> elementType == ElementType.FIELD);
        }).collect(Collectors.toList());
        // 将被@BasicParam修饰的参数存入动态bean中
        final Object beanInstance = dynamicBean.defineField(paramName, parameter.getType(), Visibility.PUBLIC)
            .annotateField(validAnnoList)
            .make()
            .load(ClassLoader.getSystemClassLoader())
            .getLoaded()
            .newInstance();
        // 给validInstance字段赋值
        ReflectUtil.setFieldValue(beanInstance, paramName, paramValue);
        // 最终通过validate进行校验
        final BeanValidationResult result = ValidationUtil.warpValidate(beanInstance);
        if (!result.isSuccess()) {
            for (BeanValidationResult.ErrorMessage message : result.getErrorMessages()) {
                throw new IllegalArgumentException(message.getMessage());
            }
        }
    }
}
