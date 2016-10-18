package project02;

public enum ServerMessage{
	MAX_BOOK_REACHED, //já atingiu 3 books
	ALREADY_BOOKED, // o livro já está bookado
	PENALISATION_ON, // o cliente eśtá com penalização ativa
	ALREADY_RESERVED, // o cliente quer renovar | emprestar  o livro, mas está reservado 
 	GIVE_BACK_BOOKS,  // o cliente possui livros que precisam ser devolvidos
	OPERATION_SUCESSFULL,  //operação requisitada bem sucedida
	BOOK_DONT_EXIST,      // o livro requisitado não existe
	ERROR,		    // erro	
	RENEWED_SUCESSFULLY //livro renovado com sucesso
}