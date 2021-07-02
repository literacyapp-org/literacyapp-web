package ai.elimu;

import ai.elimu.dao.AllophoneDao;
import ai.elimu.dao.LetterDao;
import ai.elimu.dao.LetterToAllophoneMappingDao;
import ai.elimu.model.content.Allophone;
import ai.elimu.model.content.Letter;
import ai.elimu.model.content.LetterToAllophoneMapping;
import ai.elimu.model.enums.content.allophone.SoundType;
import ai.elimu.rest.v2.crowdsource.LetterToAllophoneMappingsController;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class LetterToAllophoneMappingsControllerTest extends RestControllerTest{
    @Autowired
    LetterToAllophoneMappingDao letterToAllophoneMappingDao;

    @Autowired
    private AllophoneDao allophoneDao;

    @Autowired
    private LetterDao letterDao;

    @Autowired
    LetterToAllophoneMappingsController letterToAllophoneMappingsController;

    private MockMvc mockMvc;

    private Logger logger = LogManager.getLogger();

    @Before
    public void setup() throws JsonProcessingException {Allophone allophone = new Allophone();
        allophone.setValueIpa("ɛɛ");
        allophone.setValueSampa("EE");
        allophone.setSoundType(SoundType.VOWEL);
        allophoneDao.create(allophone);

        Letter letter = new Letter();
        letter.setDiacritic(true);
        letter.setText("c");
        letterDao.create(letter);

        List<Allophone> allophones = new ArrayList<>();
        List<Letter> letters = new ArrayList<>();

        allophones.add(allophone);
        letters.add(letter);


        LetterToAllophoneMapping letterToAllophoneMapping = new LetterToAllophoneMapping();
        letterToAllophoneMapping.setAllophones(allophones);
        letterToAllophoneMapping.setLetters(letters);

        letterToAllophoneMappingDao.create(letterToAllophoneMapping);

        mockMvc = MockMvcBuilders.standaloneSetup(letterToAllophoneMappingsController).build();
    }

    @Test
    public void testGetRequest() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/rest/v2/crowdsource/letter-to-allophone-mappings").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();

        logger.info("jsonResponse: " + jsonResponse);
        logger.info("**************************************************************************************************");

        JSONArray letterToAllophoneMappingsJSONArray = new JSONArray(jsonResponse);
        logger.info("letterToAllophoneMappingsJSONArray.length(): " + letterToAllophoneMappingsJSONArray.length());
        logger.info(letterToAllophoneMappingsJSONArray.get(0));
        assertThat(letterToAllophoneMappingsJSONArray.length() > 0, is(true));

        JSONObject letterToAllophoneMappingJsonObject = letterToAllophoneMappingsJSONArray.getJSONObject(0);
        assertThat(letterToAllophoneMappingJsonObject.getLong("id"), not(nullValue()));
        assertThat(letterToAllophoneMappingJsonObject.has("letters"), is(true));
        assertThat(letterToAllophoneMappingJsonObject.has("allophones"), is(true));
    }
}

