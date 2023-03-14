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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.cxxwl96.hiatstudio.validate.ArgumentValidatorHandler;
import com.cxxwl96.hiatstudio.validate.ValidationChain;
import com.cxxwl96.hiatstudio.validate.metadata.ValidationMetadata;
import com.cxxwl96.hiatstudio.validate.annotations.JsonParam;

import java.lang.reflect.Parameter;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

/**
 * 处理器：@JsonParam注解校验处理器
 *
 * @author cxxwl96
 * @since 2023/3/12 11:45
 */
public class JsonParamHandler implements ArgumentValidatorHandler<JsonParam> {
    private JsonParam jsonParam;

    /**
     * 初始化方法
     *
     * @param annotation 注解
     */
    @Override
    public void initialize(JsonParam annotation) {
        jsonParam = annotation;
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
        constraintIndexOutOfRange(paramName, jsonParam.index(), paramValues.size());
        // 获取输入的字符串参数
        final String paramValueString = paramValues.get(jsonParam.index());
        // 判断是否是JSON字符串
        if (!JSONUtil.isTypeJSON(paramValueString)) {
            throw new IllegalArgumentException("\"" + paramValueString + "\" is not a JSON string");
        }
        // 转换JSON字符串为对象类型
        Object paramValue = parseJsonToObject(paramValueString);
        // 若@JsonParam注解在方法参数上，则需要校验方法参数上的hibernate-validator的校验注解；若@JsonParam注解在JavaBean的字段上，会@BeanParam会自动校验
        if (parameter != null) {
            // 校验方法参数上的hibernate-validator的校验注解
            constraintHibernateValidationAnnotations(parameter, paramName, paramValue);

        }
        // TODO
        // 若通过jsonPath接收，并且接收的类型是一个正常的类，此时这个类有可能加了hibernate的校验注解，则需要再次进行校验
        if (StrUtil.isNotBlank(jsonParam.jsonPath())) {

        }
        return paramValue;
    }

    private Object parseJsonToObject(String jsonText) {
        // 是否通过jsonPath进行接收
        final String jsonPath = jsonParam.jsonPath();
        if (StrUtil.isNotBlank(jsonPath)) {
            // 解析jsonPath
            return JSONPath.read(jsonText, jsonPath);
        }
        try {
            // 是否是JSON对象
            if (JSONUtil.isTypeJSONObject(jsonText)) {
                return JSONObject.parse(jsonText);
            }
            // 是否是JSON数组
            if (JSONUtil.isTypeJSONArray(jsonText)) {
                return JSONArray.parse(jsonText);
            }
        } catch (JSONException exception) {
            throw new JSONException("\"" + jsonText + "\" cannot be converted to JSON. " + exception.getMessage());
        }
        throw new JSONException("\"" + jsonText + "\" cannot be converted to JSON");
    }
}
