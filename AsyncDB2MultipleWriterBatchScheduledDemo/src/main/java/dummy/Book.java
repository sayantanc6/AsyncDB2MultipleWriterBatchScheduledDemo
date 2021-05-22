package dummy;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Component;

@Component
@XmlRootElement(name = "book")
public class Book {

	private String author;
	
	private int ISBN;
	
	private String title; 
	
	private float price;

	public Book() {
		super();
	}

	public Book(String author, int iSBN, String title, float price) {
		super();
		this.author = author;
		ISBN = iSBN;
		this.title = title;
		this.price = price;
	}

    @XmlElement(name = "author")
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
    @XmlElement(name = "ISBN")
	public int getISBN() {
		return ISBN;
	}

	public void setISBN(int iSBN) {
		ISBN = iSBN;
	}
	


	@XmlElement(name = "title")
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    @XmlElement(name = "price")
	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Book [author=" + author + ", ISBN=" + ISBN + ", name=" + title + ", price=" + price + "]";
	}
}
