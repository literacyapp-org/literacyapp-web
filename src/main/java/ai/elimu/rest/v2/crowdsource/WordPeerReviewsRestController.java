package ai.elimu.rest.v2.crowdsource;

import ai.elimu.dao.ContributorDao;
import ai.elimu.dao.WordContributionEventDao;
import ai.elimu.dao.WordPeerReviewEventDao;
import ai.elimu.model.contributor.Contributor;
import ai.elimu.model.contributor.WordContributionEvent;
import ai.elimu.model.contributor.WordPeerReviewEvent;
import ai.elimu.model.v2.gson.crowdsource.WordContributionEventGson;
import ai.elimu.rest.v2.JpaToGsonConverter;
import ai.elimu.web.content.word.WordPeerReviewsController;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * REST API for the Crowdsource application: https://github.com/elimu-ai/crowdsource
 * <p>
 * <p>
 * This controller has similar functionality as the {@link WordPeerReviewsController}.
 */
@RestController
@RequestMapping(value = "/rest/v2/crowdsource/word-peer-reviews", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WordPeerReviewsRestController {

    private Logger logger = LogManager.getLogger();

    @Autowired
    private WordContributionEventDao wordContributionEventDao;

    @Autowired
    private ContributorDao contributorDao;

    @Autowired
    private WordPeerReviewEventDao wordPeerReviewEventDao;

    /**
     * Get {@link WordContributionEvent}s pending a {@link WordPeerReviewEvent} for the current {@link Contributor}.
     * <p>
     * Note: Currently The list of Emojis are not delivered in this method compared to the handleGetRequest method in
     * {@link WordPeerReviewsController}
     */
    @RequestMapping(method = RequestMethod.GET)
    public String handleGetRequest(HttpServletRequest request,
                                   HttpServletResponse response) {
        logger.info("handleGetRequest");


        // Validate the Contributor.
        JSONObject jsonObject = new JSONObject();

        String providerIdGoogle = request.getHeader("providerIdGoogle");
        logger.info("providerIdGoogle: " + providerIdGoogle);
        if (StringUtils.isBlank(providerIdGoogle)) {
            jsonObject.put("result", "error");
            jsonObject.put("errorMessage", "Missing providerIdGoogle");
            response.setStatus(HttpStatus.BAD_REQUEST.value());

            String jsonResponse = jsonObject.toString();
            logger.info("jsonResponse: " + jsonResponse);
            return jsonResponse;
        }

        // Lookup the Contributor by ID
        Contributor contributor = contributorDao.readByProviderIdGoogle(providerIdGoogle);
        logger.info("contributor: " + contributor);
        if (contributor == null) {
            jsonObject.put("result", "error");
            jsonObject.put("errorMessage", "The Contributor was not found.");
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());

            String jsonResponse = jsonObject.toString();
            logger.info("jsonResponse: " + jsonResponse);
            return jsonResponse;
        }

        List<WordContributionEvent> mostRecentWordContributionEvents = wordContributionEventDao.readMostRecentPerWord();
        logger.info("mostRecentWordContributionEvents.size(): " + mostRecentWordContributionEvents.size());

        // For each WordContributionEvent, check if the Contributor has already performed a peer-review.
        // If not, add it to the list of pending peer reviews.
        List<WordContributionEvent> wordContributionEventsPendingPeerReview = new ArrayList<>();
        for (WordContributionEvent mostRecentWordContributionEvent : mostRecentWordContributionEvents) {
            // Ignore WordContributionEvents made by the current Contributor
            if (mostRecentWordContributionEvent.getContributor().getId().equals(contributor.getId())) {
                continue;
            }

            // Check if the current Contributor has already peer-reviewed this Word contribution
            List<WordPeerReviewEvent> wordPeerReviewEvents = wordPeerReviewEventDao.readAll(mostRecentWordContributionEvent, contributor);
            if (wordPeerReviewEvents.isEmpty()) {
                wordContributionEventsPendingPeerReview.add(mostRecentWordContributionEvent);
            }
        }
        logger.info("wordContributionEventsPendingPeerReview.size(): " + wordContributionEventsPendingPeerReview.size());

        JSONArray wordContributionEventsJsonArray = new JSONArray();
        for (WordContributionEvent wordContributionEvent : wordContributionEventsPendingPeerReview) {
            WordContributionEventGson wordContributionEventGson = JpaToGsonConverter.getWordContributionEventGson(wordContributionEvent);
            String json = new Gson().toJson(wordContributionEventGson);
            wordContributionEventsJsonArray.put(new JSONObject(json));
        }

        String jsonResponse = wordContributionEventsJsonArray.toString();
        logger.info("jsonResponse: " + jsonResponse);
        return jsonResponse;
    }
}
