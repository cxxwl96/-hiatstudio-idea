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

package com.cxxwl96.hiatstudio.validate.custom;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * InValidator
 *
 * @author cxxwl96
 * @since 2023/3/11 18:00
 */
public class InValidator implements ConstraintValidator<In, Integer> {
    private final Set<Integer> values = new HashSet<>();

    private String msg = null;

    @Override
    public void initialize(In annotation) {
        for (int value : annotation.values()) {
            this.values.add(value);
        }
        String msg = values.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]"));
        this.msg = String.format("只能取值%s", msg);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (values.contains(value)) {
            return true;
        }
        if (context.getDefaultConstraintMessageTemplate().isEmpty()) {
            // 禁用默认的ConstraintViolation对象生成（使用在约束上声明的消息模板）
            // 用于设置不同的冲突消息或基于不同属性生成ConstraintViolation。
            context.disableDefaultConstraintViolation();
            // 构建报错消息
            context.buildConstraintViolationWithTemplate(this.msg).addConstraintViolation();
        }
        return false;
    }
}
