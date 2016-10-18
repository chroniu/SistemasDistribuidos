package project02;
import java.io.Serializable;


/**
A classe representa um livro
*/
class Book  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final long id;
	final String title;
	
	/**
	Construtor
	@param id : id do livro
	@param title : titulo do livro
	*/
	public Book(long id, String title) {
		super();
		this.id = id;
		this.title = title;
	}


	public String toString() {
		return this.title;
//		return "Book [id=" + id + ", title=" + title + "]";
	}
	 
}