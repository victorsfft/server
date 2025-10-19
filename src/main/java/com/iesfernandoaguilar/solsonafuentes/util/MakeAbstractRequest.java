package com.iesfernandoaguilar.solsonafuentes.util;

import java.io.IOException;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MakeAbstractRequest {

    public static boolean makeAbstractRequest(String cif) {
        boolean existeCif = false;

        try {
            String url = "https://vat.abstractapi.com/v1/validate/?api_key=189833adc1b64944baa2968be4528987&vat_number=" + cif;
            Content content = Request.Get(url).execute().returnContent();
            String jsonResponse = content.asString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            existeCif = root.path("valid").asBoolean();
        } catch (IOException error) {
            System.out.println("Error en la solicitud: " + error);
        }

        return existeCif;
    }
}