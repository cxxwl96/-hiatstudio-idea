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

package com.cxxwl96.hiatstudio.validate.test;

import com.cxxwl96.hiatstudio.validate.MethodRunner;
import com.cxxwl96.hiatstudio.validate.annotations.ReturnData;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * ReturnDataTest
 *
 * @author cxxwl96
 * @since 2023/3/14 19:13
 */
public class ReturnDataTest {
    @Test
    public void test() {
        MethodRunner.run(this.getClass(), "runMethod", new ArrayList<>());
    }

    private void runMethod(
        // 返回数据
        @ReturnData List<String> returnData) {

    }
}
