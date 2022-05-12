package compijava;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Analisis {
    Nodo cabeza = null, p;
    int estado = 0, columna, valorMT, numRenglon = 1;
    int caracter = 0;
    String lexema = "";
    boolean hayErrorLexico = false, hayErrorSintactico = false;
    
     //SEMANTICO//
    boolean hayErrorSemantico = false;
    boolean dentroDeStatementPart = false;
    boolean esSignoUnitario = false;
    boolean finalWriteStatement = false;
    //
    int brincoIF = 1;
    int brincoWHILE = 1;
    //
    int numeroDeOperacion = 0;
    int varCounter;
    Nodo v;
    List<variable> variableList = new ArrayList<variable>();
    List<variableUsada> variableUsadaList = new ArrayList<variableUsada>();
    List<variableNoUsada> variableNoUsadaList = new ArrayList<variableNoUsada>();
    
    List<listaInfijoPostfijo> infijoPostfijoList = new ArrayList<listaInfijoPostfijo>();
    List<pilaAuxInfijoPostfijo> infijoPostfijoPilaAux = new ArrayList<pilaAuxInfijoPostfijo>();
    
    List<pilaAuxPostfijoInfijo> postfijoInfijoPilaAux = new ArrayList<pilaAuxPostfijoInfijo>();
    
    String prioridad[][] = {
        { ":=","0"},
        {"and","1"},
        { "or","1"},
        {"not","2"},
        {  "<","3"},
        {  ">","3"},
        { "<=","3"},
        { ">=","3"},
        { "<>","3"},
        {  "=","3"},
        {  "+","4"},
        {  "-","4"},
        {  "*","5"},
        {"div","5"},
        { "+u","6"},
        { "-u","6"},
        {  "(","7"},
        {  ")","7"}
    };

    String sistemaDeTipoMasBinario [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {"integer",   "real", "string", "error"},
        /*   real 1*/ {   "real",   "real", "string", "error"},
        /* string 2*/ { "string", "string", "string", "error"},
        /*boolean 3*/ {  "error",  "error",  "error", "error"}
    };
    
    String sistemaDeTipoMenosBinario [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {"integer",   "real",  "error", "error"},
        /*   real 1*/ {   "real",   "real",  "error", "error"},
        /* string 2*/ {  "error",  "error",  "error", "error"},
        /*boolean 3*/ {  "error",  "error",  "error", "error"}
    };
    
    String sistemaDeTipoPor [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {"integer",   "real",  "error", "error"},
        /*   real 1*/ {   "real",   "real",  "error", "error"},
        /* string 2*/ {  "error",  "error",  "error", "error"},
        /*boolean 3*/ {  "error",  "error",  "error", "error"}
    };
    
    String sistemaDeTipoDiv [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {"integer",   "real",  "error", "error"},
        /*   real 1*/ {   "real",   "real",  "error", "error"},
        /* string 2*/ {  "error",  "error",  "error", "error"},
        /*boolean 3*/ {  "error",  "error",  "error", "error"}
    };
    
    String sistemaDeTipoMenorQue [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {"boolean","boolean",  "error", "error"},
        /*   real 1*/ {"boolean","boolean",  "error", "error"},
        /* string 2*/ {  "error",  "error",  "error", "error"},
        /*boolean 3*/ {  "error",  "error",  "error", "error"}
    };
    
    String sistemaDeTipoMayorQue [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {"boolean","boolean",  "error", "error"},
        /*   real 1*/ {"boolean","boolean",  "error", "error"},
        /* string 2*/ {  "error",  "error",  "error", "error"},
        /*boolean 3*/ {  "error",  "error",  "error", "error"}
    };
    
    String sistemaDeTipoMenorIgualQue [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {"boolean","boolean",  "error", "error"},
        /*   real 1*/ {"boolean","boolean",  "error", "error"},
        /* string 2*/ {  "error",  "error",  "error", "error"},
        /*boolean 3*/ {  "error",  "error",  "error", "error"}
    };
    
    String sistemaDeTipoMayorIgualQue [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {"boolean","boolean",  "error", "error"},
        /*   real 1*/ {"boolean","boolean",  "error", "error"},
        /* string 2*/ {  "error",  "error",  "error", "error"},
        /*boolean 3*/ {  "error",  "error",  "error", "error"}
    };
    
    String sistemaDeTipoIgualQue [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {"boolean","boolean",  "error", "error"},
        /*   real 1*/ {"boolean","boolean",  "error", "error"},
        /* string 2*/ {  "error",  "error",  "boolean", "error"},
        /*boolean 3*/ {  "error",  "error",  "error", "boolean"}
    };
    
    String sistemaDeTipoDiferenteQue [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {"boolean","boolean",  "error", "error"},
        /*   real 1*/ {"boolean","boolean",  "error", "error"},
        /* string 2*/ {  "error",  "error","boolean", "error"},
        /*boolean 3*/ {  "error",  "error",  "error","boolean"}
    };
    
    String sistemaDeTipoAsignacion [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {"integer",  "error",  "error", "error"},
        /*   real 1*/ {   "real",   "real",  "error", "error"},
        /* string 2*/ {  "error",  "error", "string", "error"},
        /*boolean 3*/ {  "error",  "error",  "error","boolean"}
    };
    
    String sistemaDeTipoAND [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {  "error",  "error",  "error", "error"},
        /*   real 1*/ {  "error",  "error",  "error", "error"},
        /* string 2*/ {  "error",  "error",  "error", "error"},
        /*boolean 3*/ {  "error",  "error",  "error","boolean"}
    };
    
    String sistemaDeTipoOR [][] = {
            //          integer     real     string    boolean
            //              0        1         2         3
        /*integer 0*/ {  "error",  "error",  "error", "error"},
        /*   real 1*/ {  "error",  "error",  "error", "error"},
        /* string 2*/ {  "error",  "error",  "error", "error"},
        /*boolean 3*/ {  "error",  "error",  "error","boolean"}
    };
    
    String sistemaDeTipoNOT [][] = {
            //   integer     real     string    boolean
            //       0        1         2         3
        /*0*/ {  "error",  "error",  "error","boolean"},
    };
    
    String sistemaDeTipoMasU [][] = {
            //   integer   real   string    boolean
            //     0        1       2          3
        /*0*/ {"integer", "real", "error",  "error"},
    };
    
    String sistemaDeTipoMenosU [][] = {
            //   integer   real   string     boolean
            //     0        1       2           3
        /*0*/ {"integer", "real", "error",   "error"},
    };
    
    //\SEMANTICO//
    
    String archivo = "D:\\Documents\\NetBeansProjects\\CompiJava\\src\\compijava\\codigo_Mini-PASCAL.txt";
    
    RandomAccessFile file = null;
    
    int matriz[][] = {
            //  L    D    +    -    *    =    <    >    :    .    ,    ;    (    )    {    }    "    EB   TAB  NL   EOL  EOF  OC   
            //  0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17   18   19   20   21   22
        /*0*/{  1,   2, 103, 104, 105, 106,   5,   6,   7, 113,  114, 115, 116, 117,   8, 503,   9,   0,   0,   0,   0,   0, 503},
        /*1*/{  1,   1, 100, 100, 100, 100, 100, 100, 100, 100,  100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100},
        /*2*/{101,   2, 101, 101, 101, 101, 101, 101, 101,   3,  101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101},
        /*3*/{500,   4, 500, 500, 500, 500, 500, 500, 500, 500,  500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500},
        /*4*/{102,   4, 102, 102, 102, 102, 102, 102, 102, 102,  102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102},
        /*5*/{108, 108, 108, 108, 108, 110, 108, 107, 108, 108,  108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108, 108},
        /*6*/{109, 109, 109, 109, 109, 111, 109, 109, 109, 109,  109, 109, 109, 109, 109, 109, 109, 109, 109, 109, 109, 109, 109},
        /*7*/{119, 119, 119, 119, 119, 112, 119, 119, 119, 119,  119, 119, 119, 119, 119, 119, 119, 119, 119, 119, 119, 119, 119},
        /*8*/{  8,   8,   8,   8,   8,   8,   8,   8,   8,   8,    8,   8,   8,   8,   8,   0,   8,   8,   8,   8,   8, 501,   8},
        /*9*/{  9,   9,   9,   9,   9,   9,   9,   9,   9,   9,    9,   9,   9,   9,   9,   9, 118,   9,   9,   9, 502, 501,   9},
    };
    
    String palabraReservada [][] = {
            //      0       1
        /*0*/ {    "div", "200"},
        /*1*/ {     "or", "201"},
        /*2*/ {    "and", "202"},
        /*3*/ {    "not", "203"},
        /*4*/ {   "true", "204"},
        /*5*/ {  "false", "205"},
        /*6*/ {"program", "206"},
        /*7*/ {  "begin", "207"},
        /*8*/ {    "end", "208"},
        /*9*/ {     "if", "209"},
        /*10*/{   "then", "210"},
        /*11*/{   "else", "211"},
        /*12*/{  "while", "212"},
        /*13*/{     "do", "213"},
        /*14*/{   "read", "214"},
        /*15*/{  "write", "215"},
        /*16*/{    "var", "216"},
        /*17*/{"integer", "217"},
        /*18*/{   "real", "218"},
        /*19*/{ "string", "219"}
    };
    
    String errorLexico [][] = {
            //           0             1
        /*0*/ {"Se esperaba digito", "500"},
        /*1*/ {    "EOF inesperado", "501"},
        /*2*/ {    "EOL inesperado", "502"},
        /*3*/ { "Símbolo no valido", "503"}
    };
    
    String errorSintactico [][] = {
            //                   	  0                        1
        /*0*/ {              "Se esperaba la palabra 'program'", "504"},
        /*1*/ {                     "Se esperaba identificador", "505"},
        /*2*/ {     "Se esperaba ';' después del identificador", "506"},
        /*3*/ {    "Se esperaba 'end.' para terminar el código", "507"},
        /*4*/ {                "Se esperaba la palabra 'begin'", "508"},
        /*5*/ {                  "Se esperaba la palabra 'end'", "509"},
        /*6*/ {               "Se esperaba el tipo de variable", "510"},
        /*7*/ {              "Se esperaba ';' después del tipo", "511"},
        /*8*/ {              "Se esperaba '(' después del read", "512"},
        /*9*/ {         "Se esperaba ')' después del ultimo id", "513"},
       /*10*/ {             "Se esperaba '(' después del write", "514"},
       /*11*/ {"Se esperaba ')' después de la última expresión", "515"},
       /*12*/ {   "No se esperaba nada después del punto final", "516"},
       /*13*/ {     	              "Se esperaba <statement>", "517"},
       /*14*/ {                   "Se esperaba ':=' y un valor", "518"},
       /*15*/ {    "Se esperaba 'then' después de la expresión", "519"},
       /*16*/ {      "Se esperaba 'do' después de la expresión", "520"},
       /*17*/ {                      "Se esperaba <expression>", "521"},
       /*18*/ {     "Se esperaba ':' después del identificador", "522"}
    };
    
    String errorSemantico [][] = {
            //           0                    1
        /*0*/ {"El nombre del programa no puede ser usado como identificador de variable", "523"},
        /*1*/ {    "Variable ya declarada", "524"},
        /*2*/ {    "Variable no declarada", "525"},
        /*3*/ {"Incompatibilidad de tipos", "526"}
    };
    
    public Analisis(){
        lexico();
        if (!hayErrorLexico) {
            sintactico();
            if (hayErrorSintactico) {
                //semantico();
            }
            //listaDeVariables();
            //errorSemantico();
        }
    }
    
    //                          LEXICO                          //
    private void lexico() {
        try {
            file = new RandomAccessFile(archivo,"r");
            while(caracter != -1) {
                caracter = file.read();
                if (Character.isLetter((char)caracter)) {
                    columna = 0;
                } else if (Character.isDigit((char)caracter)) {
                    columna = 1;
                } else {
                    switch(caracter) {
                        case '+':
                            columna = 2;
                            break;
                        case '-':
                            columna = 3;
                            break;
                        case '*':
                            columna = 4;
                            break;
                        case '=':
                            columna = 5;
                            break;
                        case '<':
                            columna = 6;
                            break;
                        case '>':
                            columna = 7;
                            break;
                        case ':':
                            columna = 8;
                            break;
                        case '.':
                            columna = 9;
                            break;
                        case ',':
                            columna = 10;
                            break;
                        case ';':
                            columna = 11;
                            break;
                        case '(':
                            columna = 12;
                            break;
                        case ')':
                            columna = 13;
                            break;
                        case '{':
                            columna = 14;
                            break;
                        case '}':
                            columna = 15;
                            break;
                        case '"':
                            columna = 16;
                            break;
                        case ' ':
                            columna = 17; //EB (End Bit, Espacio Blanco)
                            break;
                        case 9:
                            columna = 18; //TAB (Tabulador)
                            break;
                        case 10:
                            columna = 19; //NL (New Line, Nueva Linea)
                            numRenglon++;
                            break;
                        case 13:
                            columna = 20; //EOL (End Of Line)
                            break;
                        case -1:
                            columna = 21; //EOF (End Of File)
                            break;
                        default:
                            columna = 22;
                            break;
                    }
                }
                
                valorMT = matriz[estado][columna];
                
                if (valorMT < 100) {
                    estado = valorMT;
                    
                    if(estado == 0) {
                        lexema = "";
                    } else {
                        lexema = lexema + (char)caracter;
                    }
                } else if (valorMT >= 100 && valorMT < 500) {
                    if (valorMT == 100) {
                        validarPalabraReservada();
                    }
                    if (valorMT == 100 || valorMT == 101 || valorMT == 102 
                     || valorMT == 108 || valorMT == 109 || valorMT == 119 
                     || valorMT >= 200) {
                        file.seek(file.getFilePointer()-1);
                    } else {
                        lexema = lexema + (char)caracter;
                    }
                    insertarNodo();
                    estado = 0;
                    lexema = "";
                } else {
                    insertarNodo();
                    mensajeDeErrorLexico();
                    break;
                }   
            }
            //imprimirNodos();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    private void insertarNodo() {
        Nodo nodo = new Nodo(lexema, valorMT, numRenglon);
        
        if (cabeza == null) {
            cabeza = nodo;
            p = cabeza;
        } else {
            p.sig = nodo;
            p = nodo;
        }
    }
    
    public void imprimirNodos() {
        p = cabeza;
        System.out.println("╔════════════╦═══╦════╗");
        System.out.println("║" + String.format("%7s","") + "Lexema" + String.format("%7s","") + " ║Token║Renglón║");
        System.out.println("╠════════════╬═══╬════╣");
        while (p != null) {
            System.out.println("║" + String.format("%-20s", p.lexema) + " ║" + String.format("%4s", p.token) + " ║ " + String.format("%4s", p.renglon) + "  ║");
            p = p.sig;
        }
        System.out.println("╚════════════╩═══╩════╝");
    }
    
    private void validarPalabraReservada() {
        for (String[] palReservada : palabraReservada) {
            if (lexema.equals(palReservada[0])) {
                valorMT = Integer.valueOf(palReservada[1]);
            }
        }
    }
    
    private void mensajeDeErrorLexico() {
        if (/*caracter != -1 &&*/ valorMT >=500) {
            for (String[] eachError :  errorLexico) {
                if (valorMT == Integer.valueOf(eachError[1]) && valorMT != 501 && valorMT != 502) {
                    System.out.println("Error léxico " + valorMT + ": " + eachError[0] + ". En '" + p.lexema + "' en vez del caracter '" + (char)caracter + "' en el renglon " + numRenglon);
                } else if (valorMT == Integer.valueOf(eachError[1]) && (valorMT != 500 || valorMT != 503)) {
                    System.out.println("Error léxico " + valorMT + ": " + eachError[0] + ". En el renglon " + numRenglon);
                }
            }
        }
        hayErrorLexico = true;
    }
    
    
    //                        SINTACTICO                        //
    private void sintactico() {
        p = cabeza;
        if (p != null) {
            program();
        }
    }
    
    private void program() {
        if (p.token == 206) {
            p = p.sig;
            identifier();
            //Semantico//
            variableList.add(new variable(p.lexema,"Nombre del programa",p.renglon));
            //Fin semantico//
            p = p.sig;
            if (p.token == 115) {
                p = p.sig;
                block();
                if (p != null) {
                    if (p.token == 113) {
                        p = p.sig;
                        if (p != null) {
                            mensajeDeErrorSintactico(516);
                        }
                    } else {
                        mensajeDeErrorSintactico(507);
                    }
                }   
            } else {
                mensajeDeErrorSintactico(506);
            }
        } else {
            mensajeDeErrorSintactico(504);
        }
    }
    
    private void identifier() {
        if (p.token != 100) {
            mensajeDeErrorSintactico(505); 
        }
        //////////
        if (dentroDeStatementPart){
            if (verificarVariableDeclarada(p.lexema)) {
                variableUsadaList.add(
                        new variableUsada(p.lexema,p.renglon,true,obtenerTipo(p.lexema))
                );
            } else {
                variableUsadaList.add(
                        new variableUsada(p.lexema,p.renglon,false,obtenerTipo(p.lexema))
                );
            }
        }
        //////////
    }
    
    private void block() {
        if (p.token == 216) {
            variableDeclarationPart();
        } 
        if (p.token == 207) {
            statementPart(); 
        } else {
            mensajeDeErrorSintactico(508);
        }
    }
    
    private void variableDeclarationPart() {
        if(p.token == 216) {
            p = p.sig;
            variableDeclaration();
            while (p.token == 115) {
                p = p.sig;
                if (p.token == 100) {
                    variableDeclaration();
                }
            } 
        }
    }
    
    private void variableDeclaration() {
        identifier();
        ////
        varCounter = 1;
        v = p;
        ////
        p = p.sig;
        while (p.token == 114) {
            p = p.sig;
            identifier();
            ////
            varCounter++;
            ////
            p = p.sig;
        }
        if (p.token == 119) {
            p = p.sig;
            type();
        } else {
            mensajeDeErrorSintactico(522);
        }
    }
    
    private void type() {
        if (p.token != 217 && p.token != 218 && p.token != 219) {
            mensajeDeErrorSintactico(510);
        } else {
            ////
            for (int i = 1; i <= varCounter; i++) {
                variableList.add(new variable(v.lexema,p.lexema,p.renglon));
                v = v.sig.sig;
            }
            ////
            p = p.sig;
            if (p.token != 115) {
               mensajeDeErrorSintactico(511); 
            }
        }
    }
    
    private void statementPart() {
        if (p.token == 207) {
            dentroDeStatementPart = true;
            p = p.sig;
            statement();
            while (p.token == 115) {
                p = p.sig;
                statement();
                if (p == null) {
                    mensajeDeErrorSintactico(509);
                    break;
                }
            } 
            if (p != null) {
                if (p.token != 208) {
                    mensajeDeErrorSintactico(509);
                } else {
                    if (p.token == 208 && p.sig != null){
                        p = p.sig;
                    }
                }
            } 
            dentroDeStatementPart = false;
        } else {
            mensajeDeErrorSintactico(508);
        }
    }
    
    private void statement() {
        if (p.token == 100 || p.token == 214 || p.token == 215) {
            simpleStatement();
        } else if (p.token == 207 || p.token == 209 || p.token == 212) {
            structuredStatement();
        } else {
            mensajeDeErrorSintactico(517);
        }
    }
    
    private void simpleStatement() {
        if (p.token == 100) {
            assignmentStatement();
        } else if (p.token == 214) {
            readStatement();
        } else if (p.token == 215) {
            writeStatement();
        }
    }
    
    private void assignmentStatement() {
        numeroDeOperacion++;
        identifier();
        infijoPostfijo();
        p = p.sig;
        if (p.token == 112) {
            infijoPostfijo();
            p = p.sig;
            expression();
            infijoPostfijo();
        } else {
            mensajeDeErrorSintactico(518);
        }
    }
    
    private void readStatement() {
        if (p.token == 214) {
            p = p.sig;
            if (p.token == 116) {
                p = p.sig;
                identifier();
                //
                numeroDeOperacion++;
                infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,p.lexema,"read",p.renglon));
                infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"read","lectura",p.renglon));
                //
                p = p.sig;
                while (p.token == 114) {
                    p = p.sig;
                    identifier();
                    //
                    numeroDeOperacion++;
                    infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,p.lexema,"read",p.renglon));
                    infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"read","lectura",p.renglon));
                    //
                    p = p.sig;
                }
                if (p.token != 117) {
                    mensajeDeErrorSintactico(513);
                } else {
                    p = p.sig;
                }
            } else {
                mensajeDeErrorSintactico(512);
            }
        }
    }
    
    private void writeStatement() {
        if (p.token == 215) {
            p = p.sig;
            if (p.token == 116) {
                p = p.sig;
                numeroDeOperacion++;
                expression();
                //
                infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"write","escritura",p.renglon));
                //
                while (p.token == 114) {
                    infijoPostfijo();
                    p = p.sig;
                    numeroDeOperacion++;
                    expression();
                    //
                    if (p.token != 117) {
                        infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"write","escritura",p.renglon));
                    }
                    //
                }
                if (p.token != 117) {
                    mensajeDeErrorSintactico(515);
                } else {
                    finalWriteStatement = true;
                    infijoPostfijo();
                    //
                    if (!infijoPostfijoList.get(infijoPostfijoList.size()-1).id.equals("write")) {
                        infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"write","escritura",p.renglon));
                    }
                    //
                    finalWriteStatement = false;
                    p = p.sig;
                }
            } else {
                mensajeDeErrorSintactico(514);
            }
        }
    }
    
    private void structuredStatement() {
        if (p.token == 207) {
            statementPart();
        } else if (p.token == 209) {
            ifStatement(brincoIF);
        } else if (p.token == 212) {
            whileStatement(brincoWHILE);
        }
    }
    
    private void ifStatement(int nDeBrinco) {
        if (p.token == 209) {
            numeroDeOperacion++;
            brincoIF+=2;
            p = p.sig;
            expression();
            if (p.token == 210) {
                //<>
                numeroDeOperacion++;
                infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"BRF-I"+nDeBrinco,"brinco si es falso",p.renglon));
                //</>
                p = p.sig;
                statement();
                //<>
                numeroDeOperacion++;
                infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"BRI-I"+(nDeBrinco+1),"brinco incondicional",p.renglon));
                numeroDeOperacion++;
                infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"I"+nDeBrinco+":","etiqueta de brinco",p.renglon));
                //</>
                if (p.token == 211) {
                    numeroDeOperacion++;
                    p = p.sig;
                    statement();
                }
                //<>
                numeroDeOperacion++;
                infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"BRI-I"+(nDeBrinco+1),"brinco incondicional",p.renglon));
                numeroDeOperacion++;
                infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"I"+(nDeBrinco+1)+":","etiqueta de brinco",p.renglon));
                //</>
            } else {
                mensajeDeErrorSintactico(519);
            }       
        }
    }
    
    private void whileStatement(int nDeBrinco) {
        if (p.token == 212) {
            //<>
            brincoWHILE+=2;
            numeroDeOperacion++;
            infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"W"+(nDeBrinco+1)+":","etiqueta de brinco",p.renglon));
            //</>
            numeroDeOperacion++;
            p = p.sig;
            expression();
            //<>
            numeroDeOperacion++;
            infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"BRF-W"+nDeBrinco,"brinco si es falso",p.renglon));
            //</>
            if (p.token == 213) {
                p = p.sig;
                statement();
                //<>
                numeroDeOperacion++;
                infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"BRI-W"+(nDeBrinco+1),"brinco incondicional",p.renglon));
                //</>
            } else {
                mensajeDeErrorSintactico(520);
            }
            //<>
            numeroDeOperacion++;
            infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,"W"+nDeBrinco+":","etiqueta de brinco",p.renglon));
            //</>
        }
    }
    
    private void expression() {
        simpleExpression();
        if (p.token >= 106 && p.token <= 111) {
            infijoPostfijo();
            p = p.sig;
            simpleExpression();
        }
    }
    
    private void simpleExpression() {
        sign();
        term();
        while (p.token == 103 || p.token == 104 || p.token == 201) {
            infijoPostfijo();
            p = p.sig;
            term();
        }
    }
    
    private void sign() {
        if (p.token == 103 || p.token == 104) {
            esSignoUnitario = true;
            infijoPostfijo();
            esSignoUnitario = false;
            p = p.sig;
        }
    }
    
    private void term() {
        factor();
        p = p.sig;
        while (p.token == 105 || p.token == 200 || p.token == 202) {
            infijoPostfijo();
            p = p.sig;
            factor();
            p = p.sig;
        }      
    }
    
    private void factor() {
        if (p.token == 100) {
            identifier();
            infijoPostfijo();
        } else if (p.token == 101) {
            infijoPostfijo();//numero entero
        } else if (p.token == 102) {
            infijoPostfijo();//numero real
        } else if (p.token == 118) {
            infijoPostfijo();//string 
        } else if (p.token == 116) {
            infijoPostfijo();
            p = p.sig;
            expression();
            infijoPostfijo();
            if (p.token != 117) {
                mensajeDeErrorSintactico(515);
            } 
        } else if (p.token == 203) {
            infijoPostfijo();
            p = p.sig;
            factor();
        } else {
            mensajeDeErrorSintactico(521);
        }
    }
    
    private void mensajeDeErrorSintactico(int numError) {
        if (!hayErrorSintactico) {
            hayErrorSintactico = true;
            for (String[] eachError :  errorSintactico) {
                if (numError == Integer.valueOf(eachError[1]) && p != null) {
                    System.out.println("Error sintáctico " + eachError[1] + ": " + eachError[0] + ". En el renglon " + p.renglon + "." + p.lexema);
                } else if (numError == Integer.valueOf(eachError[1]) && p == null) {
                    System.out.println("Error sintáctico " + eachError[1] + ": " + eachError[0] + ". En el ultimo renglon.");
                }
            }
        }
    }  
    
    /////////////////// SEMANTICO ////////////
    public static class variable {
        private String id;
        private String type;
        private int renglon;
        
        private variable(String _id, String _type, int _renglon) {
            id = _id;
            type = _type;
            renglon = _renglon;
        }
    }
    
    public static class variableUsada {
        private String id;
        private int renglon;
        private boolean declarada;
        private String tipo;
        
        private variableUsada(String _id, int _renglon, boolean _declarada, String _tipo) {
            id = _id;
            renglon = _renglon;
            declarada = _declarada;
            tipo = _tipo;
        }
    }
    
    public static class variableNoUsada {
        private String id;
        private int renglon;
        private String tipo;
        
        private variableNoUsada(String _id, int _renglon, String _tipo) {
            id = _id;
            renglon = _renglon;
            tipo = _tipo;
        }
    }
    
    public String obtenerTipo(String variable){
        String tipo = "Sin Tipo";
        for (int i = 0; i <= variableList.size()-1; i++) {
            if ((variableList.get(i).id.toUpperCase()).equals(variable.toUpperCase())) {
                tipo = variableList.get(i).type;
            }
        }
        return tipo;
    }
    
    public void semantico(){
        //listaDeVariables();
        variableDuplicada(); //Verifica si hay variables duplicadas
        //listaDeVariablesUsadas();
        //llenarListaDeVariableNoUsada();
        //listaDeVariablesNoUsadas();
        variableNoDeclarada(); //Verifica si se usan variables no declaradas
        //imprimirListaPosfijo();
        //imprimirListaDePolish();
        postfijoInfijo(); //Verifica la compatibilidad de los tipos
    }
       
    public void variableDuplicada(){
        for (int i = 0; i <= variableList.size()-1; i++) {
            for (int j = i+1; j <= variableList.size()-1; j++) {
                if (i != 0) {
                    if ((variableList.get(i).id.toUpperCase()).equals(variableList.get(j).id.toUpperCase()) 
                            && !hayErrorSemantico){
                        mensajeDeErrorSemantico(524);
                        System.out.println("La variable " + variableList.get(i).id + 
                                " está repetida en el renglon " + variableList.get(j).renglon  + ".");
                    } 
                } else {
                    if ((variableList.get(i).id.toUpperCase()).equals(variableList.get(j).id.toUpperCase()) 
                            && !hayErrorSemantico){
                        mensajeDeErrorSemantico(523);
                        System.out.println("En el renglon " + variableList.get(j).renglon + ".");
                    } 
                } 
            }
        }
    }
    
    public boolean verificarVariableDeclarada(String variable){
        boolean declarada = false;
        for (int i = 1; i <= variableList.size()-1; i++) {
            if ((variableList.get(i).id.toUpperCase()).equals(variable.toUpperCase())) {
                declarada = true;
            }
        }
        return declarada;
    }
    
    public void variableNoDeclarada(){
        for (int i = 0; i <= variableUsadaList.size()-1; i++) {
            if (!variableUsadaList.get(i).declarada && !hayErrorSemantico) {
                mensajeDeErrorSemantico(525);
                System.out.println("La variable " + variableUsadaList.get(i).id 
                        + " en el renglon " + variableUsadaList.get(i).renglon 
                        + " no está declarada.");
            }
        }
    }
    
    public void llenarListaDeVariableNoUsada(){
        for (int i = 1; i <= variableList.size()-1; i++) {
            if (!verificarVariableUsada(variableList.get(i).id)) {
                variableNoUsadaList.add(new variableNoUsada(variableList.get(i).id,variableList.get(i).renglon, variableList.get(i).type));
            }
        }
    }
    
    public boolean verificarVariableUsada(String variable){
        boolean usada = false;
        for (int i = 0; i <= variableUsadaList.size()-1; i++) {
            if ((variableUsadaList.get(i).id.toUpperCase()).equals(variable.toUpperCase())) {
                usada = true;
            }
        }
        return usada;
    }
    
    private void mensajeDeErrorSemantico(int numError) {
        if (!hayErrorSemantico) {
            hayErrorSemantico = true;
            for (String[] eachError :  errorSemantico) {
                if (numError == Integer.valueOf(eachError[1])) {
                    System.out.print("Error semántico " + eachError[1] + ": " + eachError[0] + ". ");
                }
            }
        }
    }  
    
    public void listaDeVariables(){
        System.out.println("╔══════════════════════════╗");
        System.out.println("║" + String.format("%14s","") + "Lista de variables" + String.format("%13s","") + "║");
        System.out.println("╠════════╦════════════╦════╣");
        System.out.println("║" + String.format("%4s","") + "Nombre" + String.format("%4s","") + "║" + String.format("%8s","") + "Tipo" + String.format("%8s","") + "║Renglón║");
        System.out.println("╠════════╦════════════╦════╣");
        
        for(int i=0;i <= variableList.size()-1;i++){
            System.out.println("║" + String.format("%-13s", variableList.get(i).id) + " ║" + String.format("%-19s",variableList.get(i).type) + " ║" + String.format("%4s", variableList.get(i).renglon) + String.format("%3s","") + "║");
        } 
        
     
        System.out.println("╚════════╩════════════╩════╝");
    }
    
    public void listaDeVariablesUsadas(){
        
        System.out.println("╔══════════════════════════╗");
        System.out.println("║" + String.format("%10s","") + "Lista de variables usadas" + String.format("%10s","") + "║");
        System.out.println("╠════════╦════════════╦════╣");
        System.out.println("║" + String.format("%4s","") + "Nombre" + String.format("%4s","") + "║" + String.format("%8s","") + "Tipo" + String.format("%8s","") + "║Renglón║");
        System.out.println("╠════════╦════════════╦════╣");
        
        for(int i=0;i <= variableUsadaList.size()-1;i++){
            System.out.println("║" + String.format("%-13s", variableUsadaList.get(i).id) + " ║" + String.format("%-19s",variableUsadaList.get(i).tipo) + " ║" + String.format("%4s", variableUsadaList.get(i).renglon) + String.format("%3s","") + "║");
        } 
        
     
        System.out.println("╚════════╩════════════╩════╝");
    }
    
    public void listaDeVariablesNoUsadas(){
        
        System.out.println("╔══════════════════════════╗");
        System.out.println("║" + String.format("%9s","") + "Lista de variables no usadas" + String.format("%8s","") + "║");
        System.out.println("╠════════╦════════════╦════╣");
        System.out.println("║" + String.format("%4s","") + "Nombre" + String.format("%4s","") + "║" + String.format("%8s","") + "Tipo" + String.format("%8s","") + "║Renglón║");
        System.out.println("╠════════╦════════════╦════╣");
        
        for(int i=0;i <= variableNoUsadaList.size()-1;i++){
            System.out.println("║" + String.format("%-13s", variableNoUsadaList.get(i).id) + " ║" + String.format("%-19s",variableNoUsadaList.get(i).tipo) + " ║" + String.format("%4s", variableNoUsadaList.get(i).renglon) + String.format("%3s","") + "║");
        } 
        
     
        System.out.println("╚════════╩════════════╩════╝");
    }
    
    public void infijoPostfijo(){
        if (esSignoUnitario) {
            infijoPostfijoPilaAux.add(new pilaAuxInfijoPostfijo(numeroDeOperacion,p.lexema+"u","operador",p.renglon));
        } else {
            if ((p.token >= 100 && p.token <= 102) || p.token == 209 || p.token == 212 || p.token == 118) {
                if (p.token == 100) {
                    infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,p.lexema,obtenerTipo(p.lexema),p.renglon));
                }
                if (p.token == 101) {
                    infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,p.lexema,"integer",p.renglon));
                }
                if (p.token == 102) {
                    infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,p.lexema,"real",p.renglon));
                }
                if (p.token == 209) {
                    infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,p.lexema,"boolean",p.renglon));
                    infijoPostfijoPilaAux.add(new pilaAuxInfijoPostfijo(numeroDeOperacion,":=","operador",p.renglon));
                }
                if (p.token == 212) {
                    infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,p.lexema,"boolean",p.renglon));
                    infijoPostfijoPilaAux.add(new pilaAuxInfijoPostfijo(numeroDeOperacion+1,":=","operador",p.renglon));
                }
                if (p.token == 118) {
                    infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,p.lexema,"string",p.renglon));
                }
            } else if ((p.token >= 103 && p.token <= 112) || (p.token >= 200 && p.token <= 203) || p.token == 116 || p.token == 117 ) {
                if (infijoPostfijoPilaAux.size() == 0) {
                    if (!finalWriteStatement) {
                        infijoPostfijoPilaAux.add(new pilaAuxInfijoPostfijo(numeroDeOperacion,p.lexema,"operador",p.renglon));
                    }
                } else if ((infijoPostfijoPilaAux.get(infijoPostfijoPilaAux.size()-1).id).equals("(")) {
                    infijoPostfijoPilaAux.add(new pilaAuxInfijoPostfijo(numeroDeOperacion,p.lexema,"operador",p.renglon));
                } else if (p.token == 117 && !finalWriteStatement){
                    for (int i = infijoPostfijoPilaAux.size()-1; (i >= 0 && !((infijoPostfijoPilaAux.get(i).id).equals("("))); i--) {
                        infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,infijoPostfijoPilaAux.get(i).id,infijoPostfijoPilaAux.get(i).tipo,p.renglon));
                        infijoPostfijoPilaAux.remove(i);
                    }
                    if ((infijoPostfijoPilaAux.get(infijoPostfijoPilaAux.size()-1).id).equals("(")) {
                        infijoPostfijoPilaAux.remove(infijoPostfijoPilaAux.size()-1);
                    }
                } else if (p.token == 116) { 
                    infijoPostfijoPilaAux.add(new pilaAuxInfijoPostfijo(numeroDeOperacion,p.lexema,"delimitador",p.renglon));
                } else if (!finalWriteStatement) {
                    if (obtenerPrioridad(p.lexema) > obtenerPrioridad(infijoPostfijoPilaAux.get(infijoPostfijoPilaAux.size()-1).id)) {
                        infijoPostfijoPilaAux.add(new pilaAuxInfijoPostfijo(numeroDeOperacion,p.lexema,"operador",p.renglon));
                    } else {
                        infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,infijoPostfijoPilaAux.get(infijoPostfijoPilaAux.size()-1).id,infijoPostfijoPilaAux.get(infijoPostfijoPilaAux.size()-1).tipo,p.renglon));
                        infijoPostfijoPilaAux.remove(infijoPostfijoPilaAux.size()-1);
                        infijoPostfijo();
                    }
                }
            }
            if (p.sig.token == 208 || p.sig.token == 115 || p.sig.token == 210 || p.sig.token == 213 || p.token == 114 || finalWriteStatement) {
                if (!infijoPostfijoPilaAux.isEmpty()) {
                    for (int i = infijoPostfijoPilaAux.size()-1; i >= 0; i--) {
                        infijoPostfijoList.add(new listaInfijoPostfijo(numeroDeOperacion,infijoPostfijoPilaAux.get(i).id,infijoPostfijoPilaAux.get(i).tipo,p.renglon));
                        infijoPostfijoPilaAux.remove(i);
                    }
                }   
            }
        }  
    }
    
    public void postfijoInfijo(){
        int operando1=0,operando2, renglon;
        for (int i = 0; i <= infijoPostfijoList.size()-1 && !hayErrorSemantico; i++) {
            renglon = infijoPostfijoList.get(i).renglon;
            switch(infijoPostfijoList.get(i).tipo) {
                case "integer":
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo("integer",0,renglon));
                    break;
                case "real":
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo("real",1,renglon));
                    break;
                case "string":
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo("string",2,renglon));
                    break;
                case "boolean":
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo("boolean",3,renglon));
                    break;
                case "operador":
                    if (postfijoInfijoPilaAux.size()>=2) {
                        operando1 = postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).tipo;
                    }
                    operando2 = postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).tipo;
                    //System.out.println(postfijoInfijoPilaAux.size()-2);
                    evaluarEnSistemaDeTipos(operando1,operando2,i,renglon);
                    break;
                default:
                    break;
            }
        }
        if (!hayErrorSemantico) {
            //System.out.println("No hay problemas de incompatibilidad de tipos.");
        }
    }
    
    public static class listaInfijoPostfijo {
        private int contador;
        private String id;
        private String tipo;
        private int renglon;
        
        private listaInfijoPostfijo(int _contador,String _id, String _tipo, int _renglon) {
            contador = _contador;
            id = _id;
            tipo = _tipo;
            renglon = _renglon;
        }
    }
    
    public static class pilaAuxInfijoPostfijo {
        private int contador;
        private String id;
        private String tipo;
        private int renglon;
        
        private pilaAuxInfijoPostfijo(int _contador,String _id, String _tipo, int _renglon) {
            contador = _contador;
            id = _id;
            tipo = _tipo;
            renglon = _renglon;
        }
    }
    
    public static class pilaAuxPostfijoInfijo {
        private String id;
        private int tipo;
        private int renglon;
        
        private pilaAuxPostfijoInfijo(String _id, int _tipo, int _renglon) {
            id = _id;
            tipo = _tipo;
            renglon = _renglon;
        }
    }
    
    private int obtenerPrioridad(String operador) {
        int prioridadOperador = 0;
        for (String[] eachPrioridad :  prioridad) {
            if (operador.equals(String.valueOf(eachPrioridad[0]))) {
                prioridadOperador = Integer.valueOf(eachPrioridad[1]);
            } 
        }
        return prioridadOperador;
    } 
    
    public void evaluarEnSistemaDeTipos(int op1, int op2, int index, int renglon) {
        String nuevoTipo;
        switch(infijoPostfijoList.get(index).id) {
            case "+":
                nuevoTipo = sistemaDeTipoMasBinario[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una suma entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "-":
                nuevoTipo = sistemaDeTipoMenosBinario[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una resta entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "*":
                nuevoTipo = sistemaDeTipoPor[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una multiplicación entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "div":
                nuevoTipo = sistemaDeTipoDiv[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una división entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "and":
                nuevoTipo = sistemaDeTipoAND[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una comparación and entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "or":
                nuevoTipo = sistemaDeTipoOR[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una comparación or entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "not":
                nuevoTipo = sistemaDeTipoNOT[0][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible usar not en un " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "<":
                nuevoTipo = sistemaDeTipoMenorQue[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una comparación menor que entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case ">":
                nuevoTipo = sistemaDeTipoMayorQue[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una comparación mayor que entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "<=":
                nuevoTipo = sistemaDeTipoMenorIgualQue[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una comparación menor o igual que entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case ">=":
                nuevoTipo = sistemaDeTipoMayorIgualQue[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una comparacipon mayor o igual que entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id  + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "=":
                nuevoTipo = sistemaDeTipoIgualQue[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una comparación de igualdad entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "<>":
                nuevoTipo = sistemaDeTipoDiferenteQue[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible hacer una comparación diferente que entre " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " y " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case ":=":
                nuevoTipo = sistemaDeTipoAsignacion[op1][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    //postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible asignar un " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + " a un " +postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-2).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "+u":
                nuevoTipo = sistemaDeTipoMasU[0][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible usar un mas unitario en un " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            case "-u":
                nuevoTipo = sistemaDeTipoMenosU[0][op2];
                if (validarNuevoTipo(nuevoTipo)) {
                    postfijoInfijoPilaAux.remove(postfijoInfijoPilaAux.size()-1);
                    postfijoInfijoPilaAux.add(new pilaAuxPostfijoInfijo(nuevoTipo,nuevoTipoToInt(nuevoTipo),renglon));
                } else {
                    mensajeDeErrorSemantico(526);
                    System.out.println("No es posible usar un menos unitario en un " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).id + ". En el renglón " + postfijoInfijoPilaAux.get(postfijoInfijoPilaAux.size()-1).renglon + ".");
                }
                break;
            default:
                break;
        }
    }
    
    public boolean validarNuevoTipo(String nuevoTipo) {
        boolean validado = true;
        if (nuevoTipo.equals("error")) {
            validado = false;
        }
        return validado;
    }
    
    public int nuevoTipoToInt(String nuevoTipo) {
        int nuevoTipoToInt = -1;
        switch (nuevoTipo){
            case "integer":
                nuevoTipoToInt = 0;
                break;
            case "real":
                nuevoTipoToInt = 1;
                break;
            case "string":
                nuevoTipoToInt = 2;
                break;
            case "boolean":
                nuevoTipoToInt = 3;
                break;
            default:
                break;
        }
        return nuevoTipoToInt;
    }
    
    public void imprimirListaPosfijo() {
        System.out.print("1.- ");
        for (int i = 0; i <= infijoPostfijoList.size()-1; i++) {
            System.out.print(infijoPostfijoList.get(i).id + " ");
            if (infijoPostfijoList.size()-1 >= i+1) {
                if (infijoPostfijoList.get(i+1).contador > infijoPostfijoList.get(i).contador) {
                    System.out.println("");
                    System.out.print(infijoPostfijoList.get(i+1).contador + ".- ");
                }
            }   
        }
        System.out.println("");
    }
    
    public void imprimirListaDePolish() {
        System.out.print("[");
        for (int i = 0; i <= infijoPostfijoList.size()-1; i++) {
            System.out.print(infijoPostfijoList.get(i).id + ", "); 
        }
        System.out.println("]");
    }
    
    
    
    /////////GENERACIÓN DE CÓDIGO ////////////////
    List<polish> listaDePolish = new ArrayList<polish>();
    List<pilaPolish> pilaAuxPolish = new ArrayList<pilaPolish>();
    List<variableTemporal> listaDeVariables = new ArrayList<variableTemporal>();
    List<variableTemporal> listaDeVariablesTemporales = new ArrayList<variableTemporal>();
    int contadorDeVariablesTemporales = 0;
    float realVar = (float)0.00001;
    String variablesASM = "";
    String variablesTemporalesASM = "";
    String codigoASM = "";
   
    public static class polish {
        private String id;
        private String tipo;
        
        private polish(String _id, String _tipo) {
            id = _id;
            tipo = _tipo;
        }
    }
    
    public static class pilaPolish {
        private String id;
        private int tipo;
        
        private pilaPolish(String _id, int _tipo) {
            id = _id;
            tipo = _tipo;
        }
    }
    
    public static class variableTemporal {
        private String id;
        private String tipo;
        
        private variableTemporal(String _id, String _tipo) {
            id = _id;
            tipo = _tipo;
        }
    }
    
    public void generacion() {
        String dir = "C:\\DOSBox\\COMPI\\programa.asm";
        String cadena_ensamblador;
        
        File archivo = new File(dir);
        
        for (int i = 0; i <= infijoPostfijoList.size()-1; i++) {
            listaDePolish.add(new polish(infijoPostfijoList.get(i).id,infijoPostfijoList.get(i).tipo));
        }
        
        generarCodigoASM();
        
        agregarVariablesASM();
        agregarVariablesTemporalesASM();
        
        cadena_ensamblador = "" +
                "INCLUDE macros.mac\n" +
                "INCLUDE fp.a\n" +
                "INCLUDELIB STDLIB.LIB\n\n" +
                "DOSSEG\n" + 
                ".MODEL SMALL\n" +
                "STACK 100H\n" +
                ".DATA\n" +
                "\tMAXLEN DB 254\n" +
                "\tLEN DB 0\n" +
                "\tMSG DB 254 DUP(?)\n" +
                "\tMSG_DD DD MSG\n" +
                "\tBUFFER DB 8 DUP('$')\n" +
                "\tCADENA_NUM DB 10 DUP('$')\n" +
                "\tBUFFERTEMP DB 8 DUP('$')\n" +
                "\tBLANCO DB '#'\n" +
                "\tBLANCOS DB '$'\n" +
                "\tMENOS DB '-$'\n" +
                "\tCOUNT DW 0\n" +
                "\tNEGATIVO DB 0\n" +
                "\tBUF DW 10\n" +
                "\tLISTAPAR LABEL BYTE\n" +
                "\tLONGMAX DB 254\n" +
                "\tTRUE DW 1\n" +
                "\tFALSE DW 0\n" +
                "\tINTRODUCIDOS DB 254 DUP ('$')\n" +
                "\tMULT10 DW 1\n" +
                "\ts_true DB 'true$'\n" +
                "\ts_false DB 'false$'\n" +
                variablesASM +             //AGREGAR MIS VARIABLES
                variablesTemporalesASM +   //AGREGAR MIS VARIABLES TEMP
                ".CODE\n" +
                ".386\n" +
                "BEGIN:\n" +
                "\tMOV AX, @DATA\n" +
                "\tMOV DS, AX\n" +
                "CALL COMPI\n" +
                "\tMOV AX, 4C00H\n" +
                "\tINT 21H\n" +
                "COMPI PROC\n" +
                codigoASM +                //AGREGAR MI CODIGO 
                "\t\tret\n" +
                "COMPI ENDP\n" +
                "END BEGIN\n";        
       
        try {
            if (!archivo.exists()) {
                archivo.createNewFile();
            }
            FileWriter fw = new FileWriter(archivo);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(cadena_ensamblador);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try { 
          Runtime.getRuntime().exec("C:\\Program Files (x86)\\DOSBox-0.74-3\\DOSBox.exe");
        } catch (IOException ex) {
          System.out.println(ex);
        }
    }
 
    public void agregarVariablesASM(){
        for (int i = 0; i <= variableUsadaList.size()-1; i++) {
            listaDeVariables.add(new variableTemporal(variableUsadaList.get(i).id,variableUsadaList.get(i).tipo));
        }
        
        for (int i = 0; i <= listaDeVariables.size()-1; i++) {
            for (int j = i+1; j <= listaDeVariables.size()-1; j++) {
                if (listaDeVariables.get(i).id.equals(listaDeVariables.get(j).id)) {
                    listaDeVariables.remove(j);
                    j--;
                }
            }
        }

        for(int i=0;i <= listaDeVariables.size()-1;i++){
            switch (listaDeVariables.get(i).tipo){
                case "integer":
                    variablesASM = variablesASM + "\t" +
                            listaDeVariables.get(i).id + " DW 0\n";
                    break;
                case "real":
                    variablesASM = variablesASM + "\t" +
                            listaDeVariables.get(i).id + " DD " + String.format("%.5f", realVar) + "\n";
                    realVar = realVar + (float)1.00000;
                    break;
                case "string":
                    variablesASM = variablesASM + "\t" +
                            listaDeVariables.get(i).id + " DB 254 DUP('$')\n";
                    break;
                default:
                    break;
            }
        }
    }
    
    public void agregarVariablesTemporalesASM(){
        for(int i=0;i <= listaDeVariablesTemporales.size()-1;i++){
            switch (listaDeVariablesTemporales.get(i).tipo){
                case "integer":
                    variablesTemporalesASM = variablesTemporalesASM + "\t" +
                            listaDeVariablesTemporales.get(i).id + " DW 0\n";
                    break;
                case "real":
                    variablesTemporalesASM = variablesTemporalesASM + "\t" +
                            listaDeVariablesTemporales.get(i).id + " DD " + String.format("%.5f", realVar) + "\n";
                    realVar = realVar + (float)1.00000;
                    break;
                case "string":
                    variablesTemporalesASM = variablesTemporalesASM + "\t" +
                            listaDeVariablesTemporales.get(i).id + " DB 254 DUP('$')\n";
                    break;
                case "boolean":
                    variablesTemporalesASM = variablesTemporalesASM + "\t" +
                            listaDeVariablesTemporales.get(i).id + " DW 0\n";
                    break;
                default:
                    break;
            }
        }
    }
    
    public int tipoToInt(String nuevoTipo) {
        int nuevoTipoToInt = -1;
        switch (nuevoTipo){
            case "integer":
                nuevoTipoToInt = 0;
                break;
            case "real":
                nuevoTipoToInt = 1;
                break;
            case "string":
                nuevoTipoToInt = 2;
                break;
            case "boolean":
                nuevoTipoToInt = 3;
                break;
            case "operador":
                nuevoTipoToInt = 4;
                break;
            case "etiqueta de brico":
                nuevoTipoToInt = 5;
                break;
            case "brinco si es falso":
                nuevoTipoToInt = 6;
                break;
            case "brinco incondicional":
                nuevoTipoToInt = 7;
                break;
            case "escritura":
                nuevoTipoToInt = 8;
                break;
            case "lectura":
                nuevoTipoToInt = 9;
                break;
            case "read":
                nuevoTipoToInt = 10;
                break;
            default:
                break;
        }
        return nuevoTipoToInt;
    }
    
    public void generarCodigoASM(){
        int operando1 = 0,operando2;
        String tipoRead = "";
        for (int i = 0; i <= listaDePolish.size()-1; i++) {
            switch(listaDePolish.get(i).tipo) {
                case "integer":
                    pilaAuxPolish.add(new pilaPolish(listaDePolish.get(i).id,tipoToInt(listaDePolish.get(i).tipo)));
                    break;
                case "real":
                    pilaAuxPolish.add(new pilaPolish(listaDePolish.get(i).id,tipoToInt(listaDePolish.get(i).tipo)));
                    break;
                case "string":
                    pilaAuxPolish.add(new pilaPolish(listaDePolish.get(i).id,tipoToInt(listaDePolish.get(i).tipo)));
                    break;
                case "boolean":
                    pilaAuxPolish.add(new pilaPolish(listaDePolish.get(i).id,tipoToInt(listaDePolish.get(i).tipo)));
                    break;
                case "operador":
                    if (pilaAuxPolish.size()-2 >= 0) {
                        operando1 = pilaAuxPolish.get(pilaAuxPolish.size()-2).tipo;
                    }
                    operando2 = pilaAuxPolish.get(pilaAuxPolish.size()-1).tipo;
                    generarVariablesTemporales(operando1,operando2,i);
                    break;
                case "read":
                    tipoRead = obtenerTipo(listaDePolish.get(i).id);
                    pilaAuxPolish.add(new pilaPolish(listaDePolish.get(i).id,tipoToInt(tipoRead)));
                    break;
                case "lectura":
                    operando2 = pilaAuxPolish.get(pilaAuxPolish.size()-1).tipo;
                    if (operando2 == 0) { //INT
                        codigoASM += "\t" + "READ" + "\n";
                        codigoASM += "\t" + "ASCTODEC " + pilaAuxPolish.get(pilaAuxPolish.size()-1).id + "," + "MSG" + "\n";  
                    } else if (operando2 == 2) { //STRING
                        codigoASM += "\t" + "READ" + "\n";
                        codigoASM += "\t" + "S_ASIGNAR " + pilaAuxPolish.get(pilaAuxPolish.size()-1).id + "," + "MSG" + "\n";
                    }
                    pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                    break;
                case "escritura":
                    operando2 = pilaAuxPolish.get(pilaAuxPolish.size()-1).tipo;
                    if (operando2 == 0) { //INT
                        codigoASM += "\t" + "ITOA BUFFER" + "," + pilaAuxPolish.get(pilaAuxPolish.size()-1).id + "\n";
                        codigoASM += "\t" + "WRITE BUFFERTEMP" + "\n";  
                    } else if (operando2 == 2) { //STRING
                        if (listaDePolish.get(i-1).id.equals("\"\"")) {
                            codigoASM += "\t" + "WRITELN" + "\n";
                        } else {
                            contadorDeVariablesTemporales++;
                            if (pilaAuxPolish.get(pilaAuxPolish.size()-1).id.contains("\"")) {
                                //listaDeVariablesTemporales.add(new variableTemporal("t"+contadorDeVariablesTemporales, "string"));
                                variablesTemporalesASM += "\t" + "t"+contadorDeVariablesTemporales + " DB " + pilaAuxPolish.get(pilaAuxPolish.size()-1).id.substring(0, pilaAuxPolish.get(pilaAuxPolish.size()-1).id.length()-1) + "$\"" + "\n";
                                //codigoASM += "\t" + "S_ASIGNAR " + "t"+contadorDeVariablesTemporales + "," + pilaAuxPolish.get(pilaAuxPolish.size()-1).id.substring(0, pilaAuxPolish.get(pilaAuxPolish.size()-1).id.length()-1) + "$\"" + "\n";
                                codigoASM += "\t" + "WRITE " + "t"+contadorDeVariablesTemporales + "\n";
                            } else {
                                codigoASM += "\t" + "WRITE " + pilaAuxPolish.get(pilaAuxPolish.size()-1).id + "\n";
                            }  
                        }
                    }
                    pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                    break;
                case "etiqueta de brinco":
                    codigoASM += "\n\t" + listaDePolish.get(i).id + "\n";
                    break;
                case "brinco si es falso":
                    codigoASM += "\t" + "JF " + pilaAuxPolish.get(pilaAuxPolish.size()-1).id + "," + listaDePolish.get(i).id.substring(4) + "\n";
                    pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                    break;
                case "brinco incondicional":
                    codigoASM += "\t" + "JMP " + listaDePolish.get(i).id.substring(4) + "\n";
                    break;
                default:
                    break;
            }
        }
    }
    
    public void generarVariablesTemporales(int op1, int op2, int index) {
        String nuevoTipo;
        String varTemp;
        String operando1 = pilaAuxPolish.get(pilaAuxPolish.size()-2).id;
        String operando2 = pilaAuxPolish.get(pilaAuxPolish.size()-1).id;
        switch(listaDePolish.get(index).id) {
            case "+":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "SUMAR " + operando1 + "," + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoMasBinario[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case "-":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "RESTAR " + operando1 + "," + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoMenosBinario[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case "*":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "MULTI " + operando1 + "," + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoPor[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case "div":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "DIVIDE " + operando1 + "," + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoDiv[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case "and":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "I_AND " + operando1 + "," + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoAND[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case "or":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "I_OR " + operando1 + "," + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoOR[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case "not":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "I_NOT " + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoNOT[0][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case "<":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "I_MENOR " + operando1 + "," + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoMenorQue[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case ">":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "I_MAYOR " + operando1 + "," + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoMayorQue[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case "<=":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "I_MENORIGUAL " + operando1 + "," + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoMenorIgualQue[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case ">=":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "I_MAYORIGUAL " + operando1 + "," + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoMayorIgualQue[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case "=":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                if (op1 == 2 && op2 == 2) {
                    codigoASM += "\t" + "S_IGUAL " + operando1 + "," + operando2 + "," + varTemp + "\n";
                } else if (op1 == 1 && op2 == 1){
                    codigoASM += "\t" + "F_IGUAL " + operando1 + "," + operando2 + "," + varTemp + "\n";
                } else if (op1 == 0 && op2 == 0) {
                    codigoASM += "\t" + "I_IGUAL " + operando1 + "," + operando2 + "," + varTemp + "\n";
                }
                nuevoTipo = sistemaDeTipoIgualQue[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case "<>":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                if (op1 == 2 && op2 == 2) {
                    codigoASM += "\t" + "S_DIFERENTES " + operando1 + "," + operando2 + "," + varTemp + "\n";
                } else if (op1 == 1 && op2 == 1){
                    codigoASM += "\t" + "F_DIFERENTES " + operando1 + "," + operando2 + "," + varTemp + "\n";
                } else if (op1 == 0 && op2 == 0) {
                    codigoASM += "\t" + "I_DIFERENTES " + operando1 + "," + operando2 + "," + varTemp + "\n";
                }
                nuevoTipo = sistemaDeTipoDiferenteQue[op1][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            case ":=":
                if (op1 == 0 && op2 == 0) {
                    codigoASM += "\t" + "I_ASIGNAR " + operando1 + "," + operando2 + "\n";
                } else if (op1 == 1 && op2 == 1){
                    //codigoASM += "\t" + "F_ASIGNAR " + operando1 + "," + operando2 + "\n";
                } else if (op1 == 2 && op2 == 2) {
                    contadorDeVariablesTemporales++;
                    variablesTemporalesASM += "\t" + "t"+contadorDeVariablesTemporales + " DB " + pilaAuxPolish.get(pilaAuxPolish.size()-1).id.substring(0, pilaAuxPolish.get(pilaAuxPolish.size()-1).id.length()-1) + "$\"" + "\n";
                    codigoASM += "\t" + "S_ASIGNAR " + operando1 + "," + "t"+contadorDeVariablesTemporales + "\n";
                }
                //nuevoTipo = sistemaDeTipoAsignacion[op1][op2];
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op1
                break;
            case "+u":
                
                break;
            case "-u":
                contadorDeVariablesTemporales++;
                varTemp = "t"+contadorDeVariablesTemporales;
                codigoASM += "\t" + "SIGNOMENOS " + operando2 + "," + varTemp + "\n";
                nuevoTipo = sistemaDeTipoMenosU[0][op2];
                listaDeVariablesTemporales.add(new variableTemporal(varTemp, nuevoTipo));
                pilaAuxPolish.remove(pilaAuxPolish.size()-1); //op2
                pilaAuxPolish.add(new pilaPolish(varTemp,nuevoTipoToInt(nuevoTipo)));
                break;
            default:
                break;
        }
    }
    
}
