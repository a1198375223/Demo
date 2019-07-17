package com.example.processorlib;

import com.example.javalib.BindView;
import com.example.javalib.Factory;
import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.ReferenceType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.METHOD;

/**
 * 自定义一个注解处理器
 * ProcessingEnvironment
 * 1. Elements getElementUtils();                       : Elements是一个工具类，可以处理相关Element
 *                                                        (包括ExecutableElement, PackageElement, TypeElement, TypeParameterElement, VariableElement)
 *                                                        ExecutableElement     : 表示某个类或接口的方法、构造方法或初始化程序（静态或实例），包括注解类型元素。
 *                                                        PackageElement        : 表示一个包程序元素。提供对有关包及其成员的信息的访问。
 *                                                        TypeElement           : 表示一个类或接口程序元素。提供对有关类型及其成员的信息的访问。注意，枚举类型是一种类，而注解类型是一种接口。
 *                                                        TypeParameterElement  : 表示一般类、接口、方法或构造方法元素的形式类型参数。
 *                                                        VariableElement       : 表示一个字段、enum 常量、方法或构造方法参数、局部变量或异常参数。
 * 2. Messager getMessager();                           : 返回用来报告错误、警报和其他通知的 Messager。
 * 3. Filer getFiler();                                 : 用来创建新源、类或辅助文件的 Filer。
 * 4. Types getTypeUtils();                             : 返回用来在类型上进行操作的某些实用工具方法的实现。
 * 5. SourceVersion getSourceVersion();                 : 返回任何生成的源和类文件应该符合的源版本。
 * 6. Locale getLocale();                               : 返回当前语言环境；如果没有有效的语言环境，则返回 null。
 * 7. Map<String, String> getOptions();                 : 返回传递给注释处理工具的特定于 processor 的选项
 *
 * TypeMirror
 *
 *
 * AbstractProcessor
 * 1. void init(ProcessingEnvironment processingEnv);   : 该方法主要用于一些初始化的操作，通过该方法的参数ProcessingEnvironment可以获取一些列有用的工具类。
 * 2. SourceVersion getSupportedSourceVersion();        : 返回此注释 Processor 支持的最新的源版本，该方法可以通过注解@SupportedSourceVersion指定。
 * 3. getSupportedAnnotationTypes()                     : 返回此 Processor 支持的注释类型的名称，可以使用@SupportedAnnotationTypes代替
 * 4. getSupportedOptions()                             : 返回注解处理器可处理的注解操作
 * 5. process()                                         : 注解处理器的核心方法，处理具体的注解
 *
 * JavaPoet
 * 1. TypeSpec                  : 表示类、接口、或者枚举声明
 * 2. ParameterSpec             : 表示参数声明
 * 3. MethodSpec                : 表示构造函数、方法声明
 * 4. FieldSpec                 : 表示成员变量，一个字段声明
 * 5. CodeBlock                 : 表示代码块，用来拼接代码
 * 6. JavaFile                  : 表示Java类的代码
 */
@AutoService(Processor.class)
public class MyAnnotationProcessor extends AbstractProcessor {
    private static final String TAG = "MyAnnotationProcessor";
    private static final String CUSTOM_ANNOTATION = "argAnnotation";

    // 用来生成java文件的filer
    private Filer filter;

    // 用来打印信息, 在Gradle console可以看到打印信息
    private Messager messager;

    // elements接口对象, 用来操作元素
    private Elements elements;

    // 参数选项
    private Map<String, String> options;

    // Types接口, 用于操作类型的工具类
    private Types typeUtils;


    private Map<String, ClassCreatorProxy> mProxyMap = new HashMap<>();


    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // 返回该注解处理器支持的注解
        Set<String> set = new HashSet<>();
        set.add(Factory.class.getCanonicalName());
        set.add(BindView.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // 返回支持的java版本
//        return SourceVersion.RELEASE_8;
        return SourceVersion.latestSupported();
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 返回实现Elements接口的对象，用于操作元素的工具类
        elements = processingEnvironment.getElementUtils();

        // 返回指定的参数选项。
        options = processingEnvironment.getOptions();

        // 返回实现Types接口的对象，用于操作类型的工具类。
        typeUtils = processingEnvironment.getTypeUtils();

        // 返回实现Filer接口的对象，用于创建文件、类和辅助文件。
        filter = processingEnvironment.getFiler();

        // 返回实现Messager接口的对象，用于报告错误信息、警告提醒。
        messager = processingEnvironment.getMessager();

        messager.printMessage(Diagnostic.Kind.NOTE, "init annotation processor.");
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        return super.getCompletions(element, annotationMirror, executableElement, s);
    }

    @Override
    protected synchronized boolean isInitialized() {
        return super.isInitialized();
    }

    /**
     * @param set 注解类型的集合
     * @param roundEnvironment 与上一次循环有关的环境信息
     * @return 是否对注解进行处理了
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "start process");
        mProxyMap.clear();
/*
        // 获取参数选项
        String resultPath = processingEnv.getOptions().get(CUSTOM_ANNOTATION);
        if (resultPath == null) {
            messager.printMessage(Diagnostic.Kind.NOTE, "Have no argument options.");
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "Have custom argument options result path: " + resultPath);
        }

        if (set == null || set.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "process set is null.");
            return false;
        }

        for (TypeElement typeElement : set) {
            // 返回被指定注解类型注解的元素集合。
            Set<? extends Element> elementList = roundEnvironment.getElementsAnnotatedWith(typeElement);
            if (elementList != null && elementList.size() > 0) {
                for (Element e : elementList) {
                    parseElement(e);
                }

            }

            messager.printMessage(Diagnostic.Kind.NOTE, "element info: {" + "\n" +
                    "\t\tname: " + typeElement.getSimpleName() + "\n" +
                    "\t\tqualified name: " + typeElement.getQualifiedName() + "\n");
        }

        // 如果循环处理完成返回true，否则返回false。
        if (roundEnvironment.processingOver()) {
            if (!set.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Unexpected processing state: annotations still available after processing over");
                return false;
            }
        }

        
        Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();
        Map<TypeElement, String> valueMap  = new HashMap<>();
        // 通过RoundEnvironment获取到所有被@Factory注解的对象
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Factory.class)) {
            // 检测元素的有效性
            if (!SuperficialValidation.validateElement(element)) {
                messager.printMessage(Diagnostic.Kind.NOTE, "this element is invalidated. element: " + element);
                continue;
            }

            // 打印一下element的信息
            parseElement(element);

            boolean hasError = false;
            // 无论是method 还是 field 都是获取使用到@Factory注解的类对象Element
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

            // 判断类型
            if (!(element.getKind() == METHOD) || !(element.getKind() == FIELD)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format("Only filed or method can be annotated with @%s", Factory.class.getSimpleName()));
                hasError = true;
            }

            hasError |= isInaccessibleViaGeneratedCode(Factory.class, "field or method", element);
            hasError |= isInWrongPackage(Factory.class, element);


            if (hasError) {
                break;
            }

            String name = element.getSimpleName().toString();
            String value = element.getAnnotation(Factory.class).value();
            valueMap.put(enclosingElement,"@" + name + ": " + value);

            erasedTargetNames.add(enclosingElement);
        }

        Deque<Map.Entry<TypeElement, String>> entries = new ArrayDeque<>(valueMap.entrySet());

        while (!entries.isEmpty()) {
            Map.Entry<TypeElement, String> item = entries.removeFirst();

            TypeElement type = item.getKey();
            String value = item.getValue();

            TypeElement parentType = findParentType(type, erasedTargetNames);

            if (parentType == null) {
                valueMap.put(type, value);
            } else {
                String elementValue = valueMap.get(parentType);

                if (elementValue != null) {
                    valueMap.put(type, elementValue);
                } else {
                    entries.addLast(item);
                }
            }
        }


        for (Map.Entry<TypeElement, String> entry : valueMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            String value = entry.getValue();

            FieldSpec field = FieldSpec.builder(String.class, typeElement.getSimpleName() + "_TestValue",
                    Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer(value)
                    .build();

            TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(field)
                    .build();

            JavaFile javaFile = JavaFile.builder("com.example.javalib.HelloWorld", helloWorld).build();
            try {
                javaFile.writeTo(filter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/


        parseAnnotationBindView(roundEnvironment);


        for (String key : mProxyMap.keySet()) {
            ClassCreatorProxy proxyInfo = mProxyMap.get(key);
            try {
                JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(proxyInfo.getProxyClassFullName(), proxyInfo.getTypeElement());

                Writer writer = javaFileObject.openWriter();
                writer.write(proxyInfo.genJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "end process");
        return true;
    }


    private void parseAnnotationBindView(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);

        for (Element element : elements) {
            VariableElement variableElement = ((VariableElement) element);
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();

            String fullClassName = classElement.getQualifiedName().toString();

            ClassCreatorProxy proxy = mProxyMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ClassCreatorProxy(this.elements, classElement);
                mProxyMap.put(fullClassName, proxy);
            }


            BindView bindViewAnnotation = element.getAnnotation(BindView.class);

            int id = bindViewAnnotation.value();
            proxy.putElement(id, variableElement);
        }
    }



    private TypeElement findParentType(TypeElement typeElement, Set<TypeElement> parents) {
        TypeMirror type;

        while (true) {
            type = typeElement.getSuperclass();

            if (type.getKind() == TypeKind.NONE) {
                return null;
            }

            typeElement = (TypeElement) ((DeclaredType) type).asElement();

            if (parents.contains(typeElement)) {
                return typeElement;
            }
        }
    }


    /**
     * @param annotationClass 注解的class类
     * @param targetThing 字段字符串
     * @param element 被注解的元素
     * @return 是否出错
     */
    private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass, String targetThing, Element element) {
        boolean hasError = false;
        // 获取类element
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // 获取修饰符
        Set<Modifier> modifiers = element.getModifiers();

        // 不是private或者是static修饰
        if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.STATIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(), element.getSimpleName()));
            hasError = true;
        }

        // 如果不是class
        if (enclosingElement.getKind() != CLASS) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("@%s %s may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(), element.getSimpleName()));
            hasError = true;
        }

        // 如果是private修饰的class
        if (enclosingElement.getModifiers().contains(Modifier.PRIVATE)) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("@%s %s may not be contained in private classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(), element.getSimpleName()));
            hasError = true;
        }

        return hasError;
    }

    /**
     * 判断是否在错误的类中使用了注解
     * @param annotationClass 注解class对象
     * @param element 被注解的element
     * @return 是否在错误的包使用了注解
     */
    private boolean isInWrongPackage(Class<? extends Annotation> annotationClass, Element element) {

        // 获取类element
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // 获取类的全名
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        // 包名是否是以android.开头的
        if (qualifiedName.startsWith("android.")) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName));
            return true;
        }

        // 包名是否是以java.开头的
        if (qualifiedName.startsWith("java.")) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName));
            return true;
        }

        return false;
    }


    private void parseElement(Element element) {
        messager.printMessage(Diagnostic.Kind.NOTE, "start parse element -> " + element);
        if (element instanceof ExecutableElement) { // 表示某个类或接口的方法、构造方法或初始化程序（静态或实例），包括注解类型元素。
            messager.printMessage(Diagnostic.Kind.NOTE, "element belongs ExecutableElement.");
        }

        if (element instanceof PackageElement) { // 表示一个包程序元素。提供对有关包及其成员的信息的访问。
            messager.printMessage(Diagnostic.Kind.NOTE, "element belongs PackageElement.");
        }

        if (element instanceof TypeElement) { // 表示一个类或接口程序元素。提供对有关类型及其成员的信息的访问。注意，枚举类型是一种类，而注解类型是一种接口。
            messager.printMessage(Diagnostic.Kind.NOTE, "element belongs TypeElement.");
        }

        if (element instanceof TypeParameterElement) { // 表示一般类、接口、方法或构造方法元素的形式类型参数。
            messager.printMessage(Diagnostic.Kind.NOTE, "element belongs TypeParameterElement.");
        }

        if (element instanceof VariableElement) { // 表示一个字段、enum 常量、方法或构造方法参数、局部变量或异常参数。
            messager.printMessage(Diagnostic.Kind.NOTE, "element belongs VariableElement.");
        }


        ElementKind kind = element.getKind();
        if (kind == CLASS) { // 类
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is class.");
        }

        if (kind == ElementKind.ENUM) { // 枚举类
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is enum.");
        }

        if (kind == ElementKind.FIELD) { // 字段
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is field.");
        }

        if (kind == ElementKind.OTHER) { // 其他
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is other.");
        }

        if (kind == ElementKind.METHOD) { // 方法
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is method.");
        }

        if (kind == ElementKind.PACKAGE) { // 包
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is package.");
        }

        if (kind == ElementKind.INTERFACE) { // 接口
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is interface.");
        }

        if (kind == ElementKind.PARAMETER) { // 方法或者是构造方法的参数
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is parameter.");
        }

        if (kind == ElementKind.CONSTRUCTOR) { // 构造方法
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is constructor.");
        }

        if (kind == ElementKind.STATIC_INIT) { // 静态代码块
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is static init.");
        }

        if (kind == ElementKind.ENUM_CONSTANT) { // 枚举常量
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is enum constant.");
        }

        if (kind == ElementKind.INSTANCE_INIT) { // 普通代码快
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is instance init.");
        }

        if (kind == ElementKind.LOCAL_VARIABLE) { // 本地变量
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is local variable.");
        }

        if (kind == ElementKind.TYPE_PARAMETER) { // 参数的类型
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is type parameter.");
        }

        if (kind == ElementKind.ANNOTATION_TYPE) { // 注解的类型
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is annotation type.");
        }

        if (kind == ElementKind.RESOURCE_VARIABLE) { // 资源变量
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is resource variable.");
        }

        if (kind == ElementKind.EXCEPTION_PARAMETER) { // 期望的参数
            messager.printMessage(Diagnostic.Kind.NOTE, "element kind is exception parameter.");
        }

        TypeMirror type = element.asType();
        parseType(type);
    }


    private void parseType(TypeMirror type) {
        if (type instanceof ArrayType) { // 表示一个数组类型。多维数组类型被表示为组件类型也是数组类型的数组类型。
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is ArrayType.");
        }

        if (type instanceof DeclaredType) {
            // 表示某一声明类型，是一个类 (class) 类型或接口 (interface) 类型。这包括参数化的类型（比如 java.util.Set<String>）和原始类型。
            // TypeElement 表示一个类或接口元素，而 DeclaredType 表示一个类或接口类型，后者将成为前者的一种使用（或调用）。
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is DeclaredType.");
        }

        if (type instanceof ErrorType) { // 表示无法正常建模的类或接口类型。
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is ErrorType.");
        }

        if (type instanceof ExecutableType) { // 表示 executable 的类型。executable 是一个方法、构造方法或初始化程序。
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is ExecutableType.");
        }

        if (type instanceof NoType) { // 在实际类型不适合的地方使用的伪类型。
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is NoType.");
        }

        if (type instanceof NullType) { // 表示 null 类型。
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is NullType.");
        }

        if (type instanceof PrimitiveType) { // 表示一个基本类型。这些类型包括 boolean、byte、short、int、long、char、float 和 double。
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is PrimitiveType.");
        }

        if (type instanceof ReferenceType) { // 表示一个引用类型。这些类型包括类和接口类型、数组类型、类型变量和 null 类型。
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is ReferenceType.");
        }

        if (type instanceof TypeVariable) { // 表示一个类型变量。
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is TypeVariable.");
        }

        if (type instanceof WildcardType) { // 表示通配符类型参数。
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is WildcardType.");
        }

        TypeKind i = type.getKind();
        if (i == TypeKind.PACKAGE) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is package.");
        }

        if (i == TypeKind.OTHER) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is other.");
        }

        if (i == TypeKind.INT) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is int.");
        }

        if (i == TypeKind.BYTE) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is byte.");
        }

        if (i == TypeKind.CHAR) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is char.");
        }

        if (i == TypeKind.LONG) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is long.");
        }

        if (i == TypeKind.NONE) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is none.");
        }

        if (i == TypeKind.NULL) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is null.");
        }

        if (i == TypeKind.VOID) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is void.");
        }

        if (i == TypeKind.ARRAY) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is array.");
        }

        if (i == TypeKind.SHORT) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is short.");
        }

        if (i == TypeKind.ERROR) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is error.");
        }

        if (i == TypeKind.FLOAT) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is float.");
        }

        if (i == TypeKind.UNION) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is union.");
        }

        if (i == TypeKind.DOUBLE) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is double.");
        }

        if (i == TypeKind.BOOLEAN) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is boolean.");
        }

        if (i == TypeKind.TYPEVAR) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is typevar.");
        }

        if (i == TypeKind.DECLARED) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is declared.");
        }

        if (i == TypeKind.WILDCARD) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is wildcard.");
        }

        if (i == TypeKind.EXECUTABLE) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is executable.");
        }

        if (i == TypeKind.INTERSECTION) {
            messager.printMessage(Diagnostic.Kind.NOTE, "element type kind is intersection.");
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "end parse element type.\n\n\n");
    }
}
