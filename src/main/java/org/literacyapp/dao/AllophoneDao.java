package org.literacyapp.dao;

import java.util.List;
import org.literacyapp.model.Allophone;

import org.literacyapp.model.enums.Locale;

import org.springframework.dao.DataAccessException;

public interface AllophoneDao extends GenericDao<Allophone> {
	
    Allophone readByValueIpa(Locale locale, String value) throws DataAccessException;

    List<Allophone> readAllOrdered(Locale locale) throws DataAccessException;
}
