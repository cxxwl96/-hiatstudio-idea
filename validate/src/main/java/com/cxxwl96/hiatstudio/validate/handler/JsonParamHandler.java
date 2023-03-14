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
import com.cxxwl96.hiatstudio.validate.annotations.JsonParam;
import com.cxxwl96.hiatstudio.validate.metadata.ElementMetadata;
import com.cxxwl96.hiatstudio.validate.metadata.ValidationMetadata;

import java.lang.reflect.Parameter;
import java.util.List;

import cn.hutool.core.util.ClassUtil;
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
     * @param element 方法参数或类字段的元数据
     * @return 校验通过参数的值
     * @throws Exception 参数校验失败异常
     */
    @Override
    public Object handle(ValidationMetadata metadata, ValidationChain chain, ElementMetadata element) throws Exception {
        // 拦截下一个校验处理器
        chain.intercept();
        final List<String> paramValues = metadata.getParamValues(); // 输入的参数值
        final String paramName = element.getName();
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
        // 若@JsonParam注解在方法参数上，则需要校验方法参数上的hibernate-validator的校验注解；
        if (element.onParameter()) {
            // 校验方法参数上的hibernate-validator的校验注解
            constraintHibernateValidationAnnotations(element.getParameterOrField(Parameter.class), paramName,
                paramValue);
        }
        // 若接收的类型是一个标准的类，此时这个类的字段有可能加了hibernate的校验注解，则需要再次进行校验
        // 虽然可以在这个接收类型字段上添加@Valid注解进行校验，这里做了这个步骤就可以不用添加@Valid了，
        if (ClassUtil.isNormalClass(element.getType())) {
            // 需要将上面得到的paramValue类型转换为真实的类，不然上面的到的paramValue并不是用户真实创建的类（可能含有hibernate的校验注解）
            final Object beanInstance = typeCast(paramName, paramValue, element.getType());
            // 最终通过validate进行校验
            constraintHibernateValidate(beanInstance);
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
