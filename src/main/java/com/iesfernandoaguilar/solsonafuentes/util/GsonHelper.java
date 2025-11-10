package com.iesfernandoaguilar.solsonafuentes.util;

import com.google.gson.*;
import com.iesfernandoaguilar.solsonafuentes.enums.DiaSemana;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase de utilidad para crear instancias de Gson configuradas
 * con adaptadores personalizados para tipos de Java Time API
 */
public class GsonHelper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Crea una instancia de Gson con adaptadores para LocalTime y DiaSemana
     */
    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .registerTypeAdapter(DiaSemana.class, new DiaSemanaAdapter())
                .create();
    }

    /**
     * Adaptador para serializar/deserializar LocalTime
     */
    private static class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

        @Override
        public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(TIME_FORMATTER));
        }

        @Override
        public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalTime.parse(json.getAsString(), TIME_FORMATTER);
        }
    }

    /**
     * Adaptador para serializar/deserializar DiaSemana
     */
    private static class DiaSemanaAdapter implements JsonSerializer<DiaSemana>, JsonDeserializer<DiaSemana> {

        @Override
        public JsonElement serialize(DiaSemana src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.name());
        }

        @Override
        public DiaSemana deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String diaSemana = json.getAsString();
            return DiaSemana.valueOf(diaSemana.toUpperCase());
        }
    }
}
