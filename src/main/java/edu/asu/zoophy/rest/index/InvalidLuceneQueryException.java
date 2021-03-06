package edu.asu.zoophy.rest.index;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author devdemetri
 * Custom exception for invalid Lucene querystrings
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class InvalidLuceneQueryException extends Exception {

	private static final long serialVersionUID = -3278380923689647254L;
	
	public InvalidLuceneQueryException(String message) {
		super(message);
	}

}
