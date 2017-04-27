package org.literacyapp.dao;

import java.util.List;
import org.literacyapp.model.content.Word;

import org.springframework.dao.DataAccessException;

import org.literacyapp.model.content.multimedia.Image;
import org.literacyapp.model.enums.Locale;

public interface ImageDao extends GenericDao<Image> {
	
    Image read(String title, Locale locale) throws DataAccessException;

    List<Image> readAllOrdered(Locale locale) throws DataAccessException;
    
    /**
     * Fetch all Images that have been labeled by a Word.
     */
    List<Image> readAllLabeled(Word word, Locale locale) throws DataAccessException;
}
