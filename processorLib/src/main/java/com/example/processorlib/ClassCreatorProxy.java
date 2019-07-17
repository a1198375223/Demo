package com.example.processorlib;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public class ClassCreatorProxy {
    private String mBindingClassName;
    private String mPackageName;
    private TypeElement mTypeElement;
    private Map<Integer, VariableElement> mVariableElementMap = new HashMap<>();


    public ClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        this.mTypeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        this.mPackageName = packageElement.getQualifiedName().toString();
        this.mBindingClassName = mTypeElement.getSimpleName().toString() + "_ViewBinding";
    }


    public void putElement(int id, VariableElement element) {
        mVariableElementMap.put(id, element);
    }


    public String genJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(mPackageName).append("\n\n");
        builder.append("import com.example.javalib.*;\n");
        builder.append("public class ").append(mBindingClassName).append(" {\n");
        genMethods(builder);
        builder.append("}\n");
        return builder.toString();
    }


    private void genMethods(StringBuilder builder) {
        builder.append("public void bind(").append(mTypeElement.getQualifiedName()).append(" host) {\n");
        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            builder.append("host.").append(name).append(" = ");
            builder.append("(").append(type).append(") (((android.app.Activity).host)..findViewById(").append(id).append("));\n");
        }
        builder.append("}\n");
    }


    public String getProxyClassFullName() {
        return mPackageName + "." + mBindingClassName;
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }
}
