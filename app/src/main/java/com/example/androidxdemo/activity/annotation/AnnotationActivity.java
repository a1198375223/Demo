package com.example.androidxdemo.activity.annotation;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.commonlibrary.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;

/**
 * 注解处理器
 * ProcessingEnvironment
 * 1. Elements getElementUtils();                       : Elements是一个工具类，可以处理相关Element（包括ExecutableElement, PackageElement, TypeElement, TypeParameterElement, VariableElement）
 * 2. Messager getMessager();                           : 返回用来报告错误、警报和其他通知的 Messager。
 * 3. Filer getFiler();                                 : 用来创建新源、类或辅助文件的 Filer。
 * 4. Types getTypeUtils();                             : 返回用来在类型上进行操作的某些实用工具方法的实现。
 * 5. SourceVersion getSourceVersion();                 : 返回任何生成的源和类文件应该符合的源版本。
 * 6. Locale getLocale();                               : 返回当前语言环境；如果没有有效的语言环境，则返回 null。
 * 7. Map<String, String> getOptions();                 : 返回传递给注释处理工具的特定于 processor 的选项
 *
 *
 * AbstractProcessor
 * 1. void init(ProcessingEnvironment processingEnv);   : 该方法主要用于一些初始化的操作，通过该方法的参数ProcessingEnvironment可以获取一些列有用的工具类。
 * 2. SourceVersion getSupportedSourceVersion();        : 返回此注释 Processor 支持的最新的源版本，该方法可以通过注解@SupportedSourceVersion指定。
 * 3. getSupportedAnnotationTypes()                     : 返回此 Processor 支持的注释类型的名称
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
public class AnnotationActivity extends AppCompatActivity {
    private static final String TAG = "c";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(textView);
//        ButterKnife.bind(this);


        // 判断特定的class是否拥有某个注解
        boolean hasAnnotation = Test.class.isAnnotationPresent(AnnotationUtils.AnnotationTest.class);

        if (hasAnnotation) {
            // 获取注解
            AnnotationUtils.AnnotationTest classAnnotation = Test.class.getAnnotation(AnnotationUtils.AnnotationTest.class);

            if (classAnnotation == null) {
                Log.e(TAG, "Couldn't get AnnotationTest class.");
                return;
            }

            Log.d(TAG, "类类注解");
            Log.d(TAG, "id: " + classAnnotation.id());
            Log.d(TAG, "msg: " + classAnnotation.msg());
        }

        try {
            Field a = Test.class.getDeclaredField("a");
            a.setAccessible(true);
            AnnotationUtils.AnnotationTest variableAnnotation = a.getAnnotation(AnnotationUtils.AnnotationTest.class);

            if (variableAnnotation != null) {
                Log.d(TAG, "类成员注解");
                Log.d(TAG, "id: " + variableAnnotation.id());
                Log.d(TAG, "msg: " + variableAnnotation.msg());
            }

            Method testMethod = Test.class.getDeclaredMethod("method");
            if (testMethod != null) {
                testMethod.setAccessible(true);
                Annotation[] annotations = testMethod.getAnnotations();

                Log.d(TAG, "类中方法的注解");
                for (int i = 0; i < annotations.length; i++) {
                    Log.d(TAG, "类方法的注解: " + i + "->" + annotations[i].annotationType().getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @AnnotationUtils.AnnotationTest(id = 1, msg = "类Test")
    class Test {
        @AnnotationUtils.AnnotationTest(id = 2, msg = "变量a")
        int a;


        @AnnotationUtils.AnnotationTest(id = 3, msg = "方法method")
        @AnnotationUtils.AnnotationTest1(id = 4, msg = "方法method (from 2)")
        public void method() {}
    }
}
