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

package com.cxxwl96.hiatstudio.validate.utils;

import com.cxxwl96.hiatstudio.utils.ApplicationUtil;

import org.hibernate.validator.HibernateValidator;

import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.validation.BeanValidationResult;

/**
 * java bean 校验工具类，此工具类基于validation-api（jakarta.validation-api）封装
 * 在实际使用中，用户需引入validation-api的实现，如：hibernate-validator
 * 注意：hibernate-validator还依赖了javax.el，需自行引入。
 * 该工具类来自cn.hutool.hutool-all:5.7.22，拷贝出来的原因是增加语言配置，默认使用ENGLISH报错信息
 *
 * @author cxxwl96
 * @since 2023/3/11 19:13
 */
public class ValidationUtil {
    /**
     * 默认{@link Validator} 对象
     */
    private static final Validator validator;

    static {
        // 源代码为默认的validatorFactory: validator = Validation.buildDefaultValidatorFactory().getValidator();
        // 这里重新配置默认的Locale
        validator = Validation.byProvider(HibernateValidator.class).configure()
            // 快速失败模式，即有一个失败则不校验后面的属性
            .failFast(true)
            // 设置语言，默认为ENGLISH
            .defaultLocale(ApplicationUtil.getLocale()).buildValidatorFactory().getValidator();
    }

    /**
     * 获取原生{@link Validator} 对象
     *
     * @return {@link Validator} 对象
     */
    public static Validator getValidator() {
        return validator;
    }

    /**
     * 校验对象
     *
     * @param <T> Bean类型
     * @param bean bean
     * @param groups 校验组
     * @return {@link Set}
     */
    public static <T> Set<ConstraintViolation<T>> validate(T bean, Class<?>... groups) {
        return validator.validate(bean, groups);
    }

    /**
     * 校验bean的某一个属性
     *
     * @param <T> Bean类型
     * @param bean bean
     * @param propertyName 属性名称
     * @param groups 验证分组
     * @return {@link Set}
     */
    public static <T> Set<ConstraintViolation<T>> validateProperty(T bean, String propertyName, Class<?>... groups) {
        return validator.validateProperty(bean, propertyName, groups);
    }

    /**
     * 校验对象
     *
     * @param <T> Bean类型
     * @param bean bean
     * @param groups 校验组
     * @return {@link BeanValidationResult}
     */
    public static <T> BeanValidationResult warpValidate(T bean, Class<?>... groups) {
        return warpBeanValidationResult(validate(bean, groups));
    }

    /**
     * 校验bean的某一个属性
     *
     * @param <T> bean类型
     * @param bean bean
     * @param propertyName 属性名称
     * @param groups 验证分组
     * @return {@link BeanValidationResult}
     */
    public static <T> BeanValidationResult warpValidateProperty(T bean, String propertyName, Class<?>... groups) {
        return warpBeanValidationResult(validateProperty(bean, propertyName, groups));
    }

    /**
     * 包装校验结果
     *
     * @param constraintViolations 校验结果集
     * @return {@link BeanValidationResult}
     */
    private static <T> BeanValidationResult warpBeanValidationResult(Set<ConstraintViolation<T>> constraintViolations) {
        BeanValidationResult result = new BeanValidationResult(constraintViolations.isEmpty());
        for (ConstraintViolation<T> violation : constraintViolations) {
            final String propertyName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            // 消息与模板消息相同时说明自定义填写了错误消息，反之不相同说明未填写错误消息
            if (!violation.getMessage().equals(violation.getMessageTemplate())) {
                // 错误消息默认带上属性名，若为语言不为zh时添加空格
                final boolean notZh = !"zh".equals(ApplicationUtil.getLocale().getLanguage());
                errorMessage = String.format(Locale.ROOT, "%s%s%s, %s: %s.", propertyName, notZh ? " " : StrUtil.EMPTY,
                    violation.getMessage(), notZh ? "invalid value" : "无效的值", violation.getInvalidValue());
            }
            BeanValidationResult.ErrorMessage errorBean = new BeanValidationResult.ErrorMessage();
            errorBean.setPropertyName(propertyName);
            errorBean.setMessage(errorMessage);
            errorBean.setValue(violation.getInvalidValue());
            result.addErrorMessage(errorBean);
        }
        return result;
    }

}

