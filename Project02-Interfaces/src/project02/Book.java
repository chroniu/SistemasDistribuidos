package project02;

import java.io.Serializable;

class Book implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final long id;
	final String title;
	
	
	public Book(long id, String title) {
		super();
		this.id = id;
		this.title = title;
	}


	@Override
	public String toString() {
		return this.title;
//		return "Book [id=" + id + ", title=" + title + "]";
	}

}