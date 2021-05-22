package dummy;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class BookMapper implements RowMapper<Book> {

	@Override
	public Book mapRow(ResultSet rs, int rowNum) throws SQLException {  
		Book book = new Book();
		book.setAuthor(rs.getString("author")); 
		book.setISBN(rs.getInt("ISBN"));
		book.setTitle(rs.getString("title")); 
		book.setPrice(rs.getFloat("price")); 
        return book;
	}
}
