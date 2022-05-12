INCLUDE macros.mac
INCLUDE fp.a
INCLUDELIB STDLIB.LIB

DOSSEG
.MODEL SMALL
STACK 100H
.DATA
	MAXLEN DB 254
	LEN DB 0
	MSG DB 254 DUP(?)
	MSG_DD DD MSG
	BUFFER DB 8 DUP('$')
	CADENA_NUM DB 10 DUP('$')
	BUFFERTEMP DB 8 DUP('$')
	BLANCO DB '#'
	BLANCOS DB '$'
	MENOS DB '-$'
	COUNT DW 0
	NEGATIVO DB 0
	BUF DW 10
	LISTAPAR LABEL BYTE
	LONGMAX DB 254
	TRUE DW 1
	FALSE DW 0
	INTRODUCIDOS DB 254 DUP ('$')
	MULT10 DW 1
	s_true DB 'true$'
	s_false DB 'false$'
	edad DW 0
	t4 DB "Edad: $"
	t8 DB "Mayor de edad$"
	t12 DB "Menor de edad$"
	t13 DB "Edad incorrecta$"
	t1 DW 0
	t2 DW 0
	t3 DW 0
	t5 DW 0
	t6 DW 0
	t7 DW 0
	t9 DW 0
	t10 DW 0
	t11 DW 0
.CODE
.386
BEGIN:
	MOV AX, @DATA
	MOV DS, AX
CALL COMPI
	MOV AX, 4C00H
	INT 21H
COMPI PROC
	I_ASIGNAR edad,0

	W2:
	I_MAYORIGUAL edad,0,t1
	I_MENOR edad,100,t2
	I_OR t1,t2,t3
	JF t3,W1
	WRITE t4
	WRITELN
	READ
	ASCTODEC edad,MSG
	WRITELN
	I_MAYORIGUAL edad,18,t5
	I_MENORIGUAL edad,100,t6
	I_AND t5,t6,t7
	JF t7,I1
	WRITE t8
	WRITELN
	JMP I2

	I1:
	I_MENOR edad,18,t9
	I_MAYORIGUAL edad,0,t10
	I_AND t9,t10,t11
	JF t11,I3
	WRITE t12
	WRITELN
	JMP I4

	I3:
	WRITE t13
	WRITELN
	JMP I4

	I4:
	JMP I2

	I2:
	JMP W2

	W1:
		ret
COMPI ENDP
END BEGIN