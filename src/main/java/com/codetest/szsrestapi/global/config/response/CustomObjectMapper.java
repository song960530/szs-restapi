package com.codetest.szsrestapi.global.config.response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper() {
        getSerializerProvider().setNullValueSerializer(new NullSerializer()); // 빈객체는 null -> "" 처리
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 없는 필드로 인한 오류 무시
    }
}

class NullSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString("");
    }
}