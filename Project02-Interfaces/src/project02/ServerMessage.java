package project02;

public enum ServerMessage{
	MAX_BOOK_REACHED, //já atingiu 3 books
	ALREADY_BOOKED, // o livro já está bookado
	PENALISATION_ON, // o cliente eśtá com penalização ativa
	ALREADY_RESERVED, // o cliente quer renovar | emprestar  o livro, mas está reservado 
	GIVE_BACK_BOOKS,
	OPERATION_SUCESSFULL, 
	BOOK_DONT_EXIST, ERROR,
	RENEWED_SUCESSFULLY
}