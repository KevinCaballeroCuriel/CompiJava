package compijava;

import java.io.IOException;

public class CompiJava {
    public static void main(String[] args) throws IOException {
        Analisis analisis = new Analisis();
        if (!analisis.hayErrorLexico) {
            System.out.println("¡Análisis Léxico Terminado. Sin Errores!");
            if (!analisis.hayErrorSintactico) {
                System.out.println("¡Análisis Sintáctico Terminado. Sin Errores!");
                //SEMANTICO
                analisis.semantico();
                if (!analisis.hayErrorSemantico) {
                    System.out.println("¡Análisis Semántico Terminado. Sin Errores!");
                    System.out.println("Lista de Polish:");
                    analisis.imprimirListaDePolish();
                    analisis.generacion();
                    
                } else {
                    System.out.println("¡Análisis Semántico Terminado. Con Errores!");
                }
                //analisis.listaDeVariables();
                //
            } else {
                System.out.println("¡Análisis Sintáctico Terminado. Con Error!");
            }
        } else {
            System.out.println("¡Análisis Lexico Terminado. Con Error!");
        }
    }
}
