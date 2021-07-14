package ai.elimu.rest.v2.crowdsource;

import ai.elimu.util.JsonLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import selenium.DomainHelper;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WordPeerReviewsRestControllerTest {

    private Logger logger = LogManager.getLogger();

    @Test
    public void testGetRequest() {
        String jsonResponse = JsonLoader.loadJson(DomainHelper.getRestUrlV2() +
                "/crowdsource/word-peer-reviews");

        logger.info("jsonResponse: " + jsonResponse);

        JSONArray wordPeerReviewsJSONArray = new JSONArray(jsonResponse);
        logger.info("wordPeerReviewsJSONArray.length(): " + wordPeerReviewsJSONArray.length());
        assertThat(wordPeerReviewsJSONArray.length() > 0, is(true));

        JSONObject wordPeerReviewsJsonObject = wordPeerReviewsJSONArray.getJSONObject(0);
        assertThat(wordPeerReviewsJsonObject.getLong("id"), not(nullValue()));
        assertThat(wordPeerReviewsJsonObject.has("wordText"), is(true));
    }
}
