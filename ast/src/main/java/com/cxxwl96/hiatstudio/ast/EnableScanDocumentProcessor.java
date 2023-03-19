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

package com.cxxwl96.hiatstudio.ast;

import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * EnableScanDocument处理器
 * AutoService: 自动注入注解处理器
 *
 * @author cxxwl96
 * @since 2023/3/19 14:40
 */
@AutoService(Processor.class)
public class EnableScanDocumentProcessor extends AbstractProcessor {
    // 编译时期输入日志的
    private Messager messager;

    // 提供了待处理的抽象语法树
    private JavacTrees javacTrees;

    // 封装了创建AST节点的一些方法
    private TreeMaker treeMaker;

    // 提供了创建标识符的方法
    private Names names;

    /**
     * 初始化
     *
     * @param processingEnv 处理器环境
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.javacTrees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    /**
     * 支持的注解
     *
     * @return 支持的注解
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final LinkedHashSet<String> types = new LinkedHashSet<>();
        types.add(EnableInjectDocument.class.getCanonicalName());
        return types;
    }

    /**
     * 支持的Java版本
     *
     * @return 支持的Java版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 注解处理器逻辑
     *
     * @param annotations 注解
     * @param roundEnv 一轮注释处理的信息
     * @return 是否处理成功
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 获取被EnableScanDocument标注的元素
        roundEnv.getElementsAnnotatedWith(EnableInjectDocument.class).forEach(element -> {
            // 得到对应的语法树，并执行翻译
            javacTrees.getTree(element).accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl classDecl) {
                    // 获取需要被文档注入的注解
                    final String documentAnnoType = getDocumentAnnoCanonicalName(classDecl);
                    if (documentAnnoType == null) {
                        return;
                    }
                    // List<JCTree.JCMethodDecl> methodDecls = List.nil();
                    // 在抽象树中找出所有的变量
                    for (JCTree tree : classDecl.defs) {
                        // 过滤非方法
                        if (!tree.getKind().equals(Tree.Kind.METHOD)) {
                            continue;
                        }
                        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) tree;
                        // 判断方法上是否包含需要被文档注入的注解
                        for (JCTree.JCAnnotation annotation : methodDecl.getModifiers().getAnnotations()) {
                            System.out.println();
                        }

                        // methodDecls = methodDecls.append(methodDecl);
                    }
                    // 对于变量进行生成方法的操作
                    // jcVariableDeclList.forEach(jcVariableDecl -> {
                    //     messager.printMessage(Diagnostic.Kind.NOTE, jcVariableDecl.getName() + " has been processed");
                    //     classDecl.defs = classDecl.defs.prepend(makeGetterMethodDecl(jcVariableDecl));
                    // });
                    super.visitClassDef(classDecl);
                }
            });
        });
        return true;
    }

    /*
     * 获取需要被文档注入的注解类型的标准名
     */
    private String getDocumentAnnoCanonicalName(JCTree.JCClassDecl classDecl) {
        final JCTree.JCAnnotation enableInjectDocument = getAnnotation(classDecl.getModifiers().getAnnotations(),
            EnableInjectDocument.class);
        if (enableInjectDocument == null) {
            return null;
        }
        // 得到：value = 需要被文档注入的注解.class
        final JCTree.JCExpression expression = enableInjectDocument.getArguments().get(0);
        // 得到：java.lang.Class<需要被文档注入的注解>
        final Type type = ((JCTree.JCAssign) expression).rhs.type;
        // 得到：需要被文档注入的注解全限定名。如com.xx.xxAnnotation
        return type.allparams().get(0).toString();
    }

    // private JCTree.JCMethodDecl makeGetterMethodDecl(JCTree.JCVariableDecl jcVariableDecl) {
    //     ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
    //     // 生成表达式 例如 this.a = a;
    //     JCTree.JCExpressionStatement aThis = makeAssignment(
    //         treeMaker.Select(treeMaker.Ident(names.fromString("this")), jcVariableDecl.getName()),
    //         treeMaker.Ident(jcVariableDecl.getName()));
    //     statements.append(aThis);
    //     JCTree.JCBlock block = treeMaker.Block(0, statements.toList());
    //
    //     // 生成入参
    //     JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), jcVariableDecl.getName(),
    //         jcVariableDecl.vartype, null);
    //     List<JCTree.JCVariableDecl> parameters = List.of(param);
    //
    //     // 生成返回对象
    //     JCTree.JCExpression methodType = treeMaker.Type(new Type.JCVoidType());
    //     return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), getNewMethodName(jcVariableDecl.getName()),
    //         methodType, List.nil(), parameters, List.nil(), block, null);
    //
    // }
    //
    // private Name getNewMethodName(Name name) {
    //     String s = name.toString();
    //     return names.fromString("get" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
    // }
    //
    // private JCTree.JCExpressionStatement makeAssignment(JCTree.JCExpression lhs, JCTree.JCExpression rhs) {
    //     return treeMaker.Exec(treeMaker.Assign(lhs, rhs));
    // }
    //
    // private boolean hasAnnotation(List<JCTree.JCAnnotation> annotations, Class<? extends Annotation> annotationClass) {
    //     return getAnnotation(annotations, annotationClass) != null;
    // }

    private JCTree.JCAnnotation getAnnotation(List<JCTree.JCAnnotation> annotations,
        Class<? extends Annotation> annotationClass) {
        for (JCTree.JCAnnotation annotation : annotations) {
            if (annotation.getAnnotationType().type.toString().equals(annotationClass.getCanonicalName())) {
                return annotation;
            }
        }
        return null;
    }
}