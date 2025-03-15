package com.sentury.approvalflow.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.utils.ReflectionsCache;
import org.reflections.Reflections;

import java.util.Set;

/**
 * @author zhj
 * @version 1.0
 * @description: TODO
 * @date 2024/4/4 17:50
 */
public class NodeJacksonTypeIdResolver implements TypeIdResolver {
    private JavaType baseType;

    @Override
    public void init(JavaType javaType) {
        this.baseType = javaType;
    }


    @Override
    public String idFromValue(Object o) {
        return idFromValueAndType(o, o.getClass());
    }


    /**
     * 序列化时填充什么对象
     */
    @Override
    public String idFromValueAndType(Object o, Class<?> aClass) {
        //有出现同名类时可以用这个来做区别
        JsonTypeName annotation = aClass.getAnnotation(JsonTypeName.class);
        if (annotation != null) {
            return annotation.value();
        }
        return "";
    }


    /**
     * idFromValueAndType 是序列化的时候告诉序列化器怎么生成标识符
     * <p>
     * typeFromId是反序列化的时候告诉序列化器怎么根据标识符来识别到具体类型，这里用了反射,在程序启动时，把要加载的包通过Reflections加载进来
     */
    @Override
    public JavaType typeFromId(DatabindContext databindContext, String type) {
        Class<?> clazz = getSubType(type);
        if (clazz == null) {

             throw new IllegalStateException("cannot find class '" + type + "'");
        }
        return databindContext.constructSpecializedType(baseType, clazz);
    }

    public Class<?> getSubType(String type) {
        Reflections reflections = ReflectionsCache.getReflections();
        Set<Class<?>> subTypes = reflections.getSubTypesOf((Class<Object>) baseType.getRawClass());
        for (Class<?> subType : subTypes) {
            JsonTypeName annotation = subType.getAnnotation(JsonTypeName.class);
            if (annotation != null && annotation.value().equals(type)) {
                return subType;
            } else if (subType.getSimpleName().equals(type) || subType.getName().equals(type)) {
                return subType;
            }
        }
//        if(StrUtil.isBlank(type)||StrUtil.equalsAnyIgnoreCase(type,"null","node")){
//            return Node.class;
//        }
        return Node.class;
    }

    @Override
    public String idFromBaseType() {
        return idFromValueAndType(null, baseType.getClass());
    }

    @Override
    public String getDescForKnownTypeIds() {
        return null;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;

    }
}
