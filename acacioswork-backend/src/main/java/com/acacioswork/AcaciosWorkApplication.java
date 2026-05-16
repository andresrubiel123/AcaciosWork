/** Clase principal que inicia el backend de AcaciosWork. @author RADJ */
package com.acacioswork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.acacioswork")
public class AcaciosWorkApplication {

    /** Punto de entrada principal de la aplicación. @author RADJ */
    public static void main(String[] args) {
        SpringApplication.run(AcaciosWorkApplication.class, args);
    }
}
