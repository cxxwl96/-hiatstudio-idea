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
import com.cxxwl96.hiatstudio.validate.ArgumentValidatorHandler;
import com.cxxwl96.hiatstudio.validate.ValidationChain;
import com.cxxwl96.hiatstudio.validate.ValidationMetadata;
import com.cxxwl96.hiatstudio.validate.annotations.JsonParam;
import com.cxxwl96.hiatstudio.validate.annotations.ValidatorHandler;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Locale;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理器：@JsonParam注解校验处理器
 *
 * @author cxxwl96
 * @since 2023/3/12 11:45
 */
@Slf4j
@ValidatorHandler(annotation = JsonParam.class)
public class JsonParamHandler implements ArgumentValidatorHandler {
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
        final JsonParam jsonParam = parameter.getAnnotation(JsonParam.class);
        // 校验参数取值是否越界
        if (jsonParam.index() < 0 || jsonParam.index() >= paramValues.size()) {
            final String error = String.format(Locale.ROOT,
                "Out of range. There are only %d input parameters, but \"%s\" takes a %d parameter.",
                paramValues.size(), paramName, jsonParam.index() + 1);
            throw new IndexOutOfBoundsException(error);
        }
        // 获取输入的字符串参数
        final String paramValueString = paramValues.get(jsonParam.index());
        // 判断是否是JSON字符串
        if (!JSONUtil.isTypeJSON(paramValueString)) {
            throw new IllegalArgumentException("\"" + paramValueString + "\" is not a JSON string");
        }
        try {
            // 是否是JSON对象
            if (JSONUtil.isTypeJSONObject(paramValueString)) {
                return JSONObject.parse(paramValueString);
            }
            // 是否是JSON数组
            if (JSONUtil.isTypeJSONArray(paramValueString)) {
                return JSONArray.parse(paramValueString);
            }
        } catch (JSONException exception) {
            throw new JSONException(
                "\"" + paramValueString + "\" cannot be converted to JSON. " + exception.getMessage());
        }
        throw new JSONException("\"" + paramValueString + "\" cannot be converted to JSON");

    }
}
