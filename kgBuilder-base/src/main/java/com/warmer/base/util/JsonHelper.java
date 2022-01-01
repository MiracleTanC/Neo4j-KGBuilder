package com.warmer.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.ArrayList;

/**
 * json工具类
 * @author yindm
 */
public class JsonHelper {
    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 反序列化成对象
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> T parseObject(String json, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(json, clazz);
    }

    /**
     * 反序列化成List
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> ArrayList<T> parseArray(String json, Class<T> clazz) throws JsonProcessingException {
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        return mapper.readValue(json, listType);
    }

    /**
     * 反序列化成对象
     * @param json
     * @param type
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> T parseObject(String json, TypeReference<T> type) throws JsonProcessingException {
        return mapper.readValue(json, type);
    }

    /**
     * 反序列化成对象
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    public static JsonNode parseObject(String json) throws JsonProcessingException {
        return mapper.readTree(json);
    }

    /**
     * 序列化成对象
     * @param value
     * @return
     * @throws JsonProcessingException
     */
    public static String toJSONString(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    /**
     * 对象转换成List
     * @param value
     * @param <T>
     * @return
     */
    public static <T extends JsonNode> T valueToTree(Object value) {
        return mapper.valueToTree(value);
    }

    /**
     * 创建Json节点
     * @return
     */
    public static ObjectNode createNode() {
        return mapper.createObjectNode();
    }

    /**
     * 创建JsonList
     * @return
     */
    public static ArrayNode createArray() {
        return mapper.createArrayNode();
    }
}
