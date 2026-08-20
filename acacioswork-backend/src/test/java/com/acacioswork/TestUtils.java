/** Utilidades para pruebas unitarias. @author RADJ */
package com.acacioswork;

import com.fasterxml.jackson.databind.ObjectMapper;

/** Clase utilitaria para configurar ObjectMapper con módulos de fecha/hora para pruebas. @author RADJ */
public class TestUtils {

    /** Retorna un ObjectMapper configurado para serializar tipos de Java 8 como LocalDateTime. @author RADJ */
    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }
}
