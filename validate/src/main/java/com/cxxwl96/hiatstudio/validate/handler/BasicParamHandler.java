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
import com.cxxwl96.hiatstudio.validate.metadata.ValidationMetadata;
import com.cxxwl96.hiatstudio.validate.annotations.BasicParam;

import java.lang.reflect.Parameter;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 处理器：@BasicParam注解校验处理器
 *
 * @author cxxwl96
 * @since 2023/3/3 15:49
 */
@Slf4j
public class BasicParamHandler implements ArgumentValidatorHandler<BasicParam> {
    private BasicParam basicParam;

    /**
     * 初始化方法
     *
     * @param annotation 注解
     */
    @Override
    public void initialize(BasicParam annotation) {
        basicParam = annotation;
    }

    /**
     * 参数校验处理
     *
     * @param metadata 校验元数据
     * @param chain 校验链
     * @param parameter 参数
     * @param index 参数索引
     * @param paramName 参数名
     * @return 校验通过参数的值
     * @throws Exception 参数校验失败异常
     */
    @Override
    public Object handle(ValidationMetadata metadata, ValidationChain chain, Parameter parameter, int index,
        String paramName) throws Exception {
        // 拦截下一个校验处理器
        chain.intercept();
        final List<String> paramValues = metadata.getParamValues(); // 输入的参数值
        // 校验参数取值是否越界
        constraintIndexOutOfRange(paramName, basicParam.index(), paramValues.size());
        // 获取输入的字符串参数
        final String paramValueString = paramValues.get(basicParam.index());
        // 参数值类型转换
        Object paramValue = typeCast(paramName, paramValueString, parameter.getType());
        // 非必填直接返回参数值，不做校验
        if (!basicParam.require()) {
            return paramValue;
        }
        // 校验方法参数上的hibernate-validator的校验注解
        constraintHibernateValidationAnnotations(parameter, paramName, paramValue);
        // 校验通过则返回参数值
        return paramValue;
    }
}
