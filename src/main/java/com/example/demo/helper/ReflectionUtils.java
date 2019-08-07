package com.example.demo.helper;

import javassist.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author xuliduo
 * @date 2019/7/3
 * @description class ReflectionUtils
 */
public class ReflectionUtils<E> {
    public static final String TABLE_SUFFIX = "_changed";
    private static final Map<String, AtomicLong> VERSION = new HashMap<>();

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private CtClass ctClass;
    private E e;
    private Object object;
    private Map<String, Object> map;

    /**
     * 通过已有changed对象获取反射工具
     *
     * @param e 需要添加changed的原始类
     * @throws NotFoundException class not found
     */
    public ReflectionUtils(E e) throws NotFoundException {
        assert e != null;
        this.e = e;
        if (VERSION.get(e.getClass().getCanonicalName()) == null) {
            VERSION.put(e.getClass().getCanonicalName(), new AtomicLong());
        }
        if (VERSION.get(e.getClass().getCanonicalName()).get() > 0) {
            ctClass = ClassPool.getDefault().get(e.getClass().getCanonicalName() + TABLE_SUFFIX + VERSION.get(e.getClass().getCanonicalName()).get());
        }
    }


    /**
     * 通过原始对象获取反射工具（新添加的类会加上_changed后缀）
     * 当前版本只支持新增String、整形、浮点型
     *
     * @param cls 原始对象类
     * @throws NotFoundException      cls is null
     * @throws IllegalAccessException 生成新类失败
     * @throws InstantiationException 生成新类失败
     */
    public ReflectionUtils(Class<E> cls) throws NotFoundException, IllegalAccessException, InstantiationException {
        assert cls != null;
        this.e = cls.newInstance();
        if (VERSION.get(e.getClass().getCanonicalName()) == null) {
            VERSION.put(e.getClass().getCanonicalName(), new AtomicLong());
        }
        ctClass = ClassPool.getDefault().get(e.getClass().getCanonicalName());
//        ctClass.setName(ctClass.getName() + TABLE_SUFFIX + VERSION.get());
    }

    /**
     * 为对象填充属性（如果 key对应的field不存在，就会跳过）
     *
     * @param map          map
     * @param idFieldsName _id对应的java类 @id 字段 ， 默认为 "id"
     * @return E
     */
    public E writerFileds2(Map<String, Object> map, String idFieldsName) {
        assert e != null;
        writer(map, idFieldsName, e);
        return e;
    }

    /**
     * @param map
     * @param idFieldsName
     * @param e
     */
    private void writer(Map<String, Object> map, String idFieldsName, Object e) {
        if (idFieldsName == null) {
            idFieldsName = "id";
        }
        String finalIdFieldsName = idFieldsName;
        map.forEach((key, value) -> {
            try {
                if ("_id".equals(key)) {
                    FieldUtils.writeField(e, finalIdFieldsName, value, true);
                } else {
                    FieldUtils.writeField(e, key, value, true);
                }
            } catch (IllegalAccessException | IllegalArgumentException e1) {
                log.error(e1.getMessage(), e1);
            }
        });
    }


    /**
     * 动态根据数据为对象添加Field
     *
     * @param map 数据
     * @return this
     */
    public ReflectionUtils builder(Map<String, Object> map) throws NotFoundException {
        this.map = map;
        // 获取当前版本的changed_class
        Class aClass;
        try {
            aClass = Class.forName(e.getClass().getCanonicalName() + TABLE_SUFFIX + VERSION.get(e.getClass().getCanonicalName()).get());
        } catch (ClassNotFoundException e1) {
            aClass = e.getClass();
        }
        Class finalAClass = aClass;
        // 收集这次变化字段
        Map<String, String> moreFields = new HashMap<>();
        map.keySet().forEach(key -> {
            if ("_id".equals(key)) return;
            if (FieldUtils.getField(finalAClass, key, true) == null) {
                moreFields.put(key, map.get(key).getClass().getCanonicalName());
            }
        });
        if (moreFields.size() > 0) {
            // 收集当前版本class和原始版本class的字段变化
            if (!e.getClass().getCanonicalName().equals(aClass.getCanonicalName())) {
                List<Field> olds = FieldUtils.getAllFieldsList(e.getClass());
                List<Field> nows = FieldUtils.getAllFieldsList(aClass);
                moreFields.putAll(nows.stream()
                        .filter(now -> olds.stream().noneMatch(old -> now.getName().equals(old.getName())))
                        .collect(Collectors.toMap(Field::getName, x -> x.getType().getCanonicalName())));
            }
            ctClass = ClassPool.getDefault().get(e.getClass().getCanonicalName());
            // 重写class
            ctClass.setName(e.getClass().getCanonicalName() + TABLE_SUFFIX + VERSION.get(e.getClass().getCanonicalName()).incrementAndGet());
            // 再次循环填写字段
            moreFields.forEach((key, type) -> {
                if ("_id".equals(key)) return;
                try {
                    if (FieldUtils.getField(e.getClass(), key, true) == null) {
                        CtField f = new CtField(ClassPool.getDefault().get(type), key, ctClass);
                        ctClass.addField(f);
                        ctClass.addMethod(CtNewMethod.getter("get" + StringUtils.capitalize(key), f));
                    }
                } catch (NotFoundException | CannotCompileException e1) {
                    log.error(e1.getMessage(), e1);
                }
            });
        }
        ctClass.defrost();
        return this;
    }

    /**
     * 为对象填充属性
     * 该方法需要先进行builder 创建一个新的类 xxx_changed
     *
     * @param idFieldsName _id对应的java类 @id 字段 ， 默认为 "id"
     * @return e
     */
    public Object writerFileds(String idFieldsName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (map == null) {
            throw new NullPointerException("数据为空,请先调用builder(...)方法");
        }
        try {
            object = ctClass.toClass().newInstance();
        } catch (CannotCompileException e1) {
            object = Class.forName(ctClass.getName()).newInstance();
        }
        // log.debug("new class-> {}", object.getClass().getCanonicalName());
        writer(map, idFieldsName, object);
        ctClass.defrost();
        return object;
    }

    /**
     * 获取E对应的change类的对象，并填充map里面的数据，需要其已存在
     * ！！！不支持static和final字段！！！
     *
     * @param idFieldsName _id对应的java类 @id 字段 ， 默认为 "id"
     * @param map          map
     * @return E_change
     */
    public Object getChangedObject(String idFieldsName, Map<String, Object> map) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (VERSION.get(e.getClass().getCanonicalName()).get() == 0) {
            return e;
        }
        object = Class.forName(e.getClass().getCanonicalName() + TABLE_SUFFIX + VERSION.get(e.getClass().getCanonicalName()).get()).newInstance();
        // 先克隆已有数据
        FieldUtils.getAllFieldsList(e.getClass()).forEach(field -> {
            try {
                if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                    FieldUtils.writeField(object, field.getName(), FieldUtils.readField(e, field.getName(), true), true);
                }
            } catch (IllegalAccessException e1) {
                log.error(e1.getMessage(), e1);
            }
        });
        // 处理对象数据
        writer(map, idFieldsName, object);
        return object;
    }
}
