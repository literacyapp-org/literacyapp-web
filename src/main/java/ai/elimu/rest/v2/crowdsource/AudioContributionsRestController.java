package ai.elimu.rest.v2.crowdsource;

import ai.elimu.dao.AudioContributionEventDao;
import ai.elimu.dao.AudioDao;
import ai.elimu.dao.ContributorDao;
import ai.elimu.dao.WordDao;
import ai.elimu.model.content.Word;
import ai.elimu.model.content.multimedia.Audio;
import ai.elimu.model.contributor.AudioContributionEvent;
import ai.elimu.model.contributor.Contributor;
import ai.elimu.model.enums.content.AudioFormat;
import ai.elimu.model.v2.gson.content.WordGson;
import ai.elimu.rest.v2.JpaToGsonConverter;
import ai.elimu.util.CrowdsourceHelper;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/rest/v2/crowdsource/audio-contributions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AudioContributionsRestController {
 
    private Logger logger = LogManager.getLogger();
    
    @Autowired
    private ContributorDao contributorDao;
    
    @Autowired
    private WordDao wordDao;
    
    @Autowired
    private AudioDao audioDao;
    
    @Autowired
    private AudioContributionEventDao audioContributionEventDao;
    
    /**
     * Get {@link Word}s pending {@link Audio} recording for the current {@link Contributor}.
     */
    @RequestMapping(value = "/words", method = RequestMethod.GET)
    public String handleGetWordsRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        logger.info("handleGetWordsRequest");
        
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
        
        // Get the IDs of Words that have already been recorded by the Contributor
        List<AudioContributionEvent> audioContributionEvents = audioContributionEventDao.readAll(contributor);
        logger.info("audioContributionEvents.size(): " + audioContributionEvents.size());
        HashMap<Long, Void> idsOfRecordedWordsHashMap = new HashMap<>();
        for (AudioContributionEvent audioContributionEvent : audioContributionEvents) {
            Audio audio = audioContributionEvent.getAudio();
            Word word = audio.getWord();
            if (word != null) {
                idsOfRecordedWordsHashMap.put(word.getId(), null);
            }
        }
        logger.info("idsOfRecordedWordsHashMap.size(): " + idsOfRecordedWordsHashMap.size());
        
        // For each Word, check if the Contributor has already contributed a 
        // corresponding Audio recording. If not, add it to the list of pending recordings.
        List<Word> wordsPendingAudioRecording = new ArrayList<>();
        for (Word word : wordDao.readAllOrderedByUsage()) {
            if (!idsOfRecordedWordsHashMap.containsKey(word.getId())) {
                wordsPendingAudioRecording.add(word);
            }
        }
        logger.info("wordsPendingAudioRecording.size(): " + wordsPendingAudioRecording.size());
        
        // Convert to JSON
        JSONArray wordsJsonArray = new JSONArray();
        for (Word word : wordsPendingAudioRecording) {
            WordGson wordGson = JpaToGsonConverter.getWordGson(word);
            String json = new Gson().toJson(wordGson);
            wordsJsonArray.put(new JSONObject(json));
        }
        
        String jsonResponse = wordsJsonArray.toString();
        logger.info("jsonResponse: " + jsonResponse);
        return jsonResponse;
    }
    
    @RequestMapping(value = "/words", method = RequestMethod.POST)
    public String handleUploadWordRecordingRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("file") MultipartFile multipartFile
    ) {
        logger.info("handleUploadWordRecordingRequest");
        
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
        
        String filename = multipartFile.getName();
        logger.info("filename: " + filename);
        
        // Expected format: "word_5.mp3"
        String originalFilename = multipartFile.getOriginalFilename();
        logger.info("originalFilename: " + originalFilename);
        
        AudioFormat audioFormat = CrowdsourceHelper.extractAudioFormatFromFilename(filename);
        logger.info("audioFormat: " + audioFormat);
        
        Long wordIdExtractedFromFilename = CrowdsourceHelper.extractWordIdFromFilename(filename);
        logger.info("wordIdExtractedFromFilename: " + wordIdExtractedFromFilename);
        Word word = wordDao.read(wordIdExtractedFromFilename);
        logger.info("word: " + word);
        if (word == null) {
            jsonObject.put("result", "error");
            jsonObject.put("errorMessage", "A Word with ID " + wordIdExtractedFromFilename + " was not found.");
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            
            String jsonResponse = jsonObject.toString();
            logger.info("jsonResponse: " + jsonResponse);
            return jsonResponse;
        }
        
        String contentType = multipartFile.getContentType();
        logger.info("contentType: " + contentType);
        
        try {
            byte[] bytes = multipartFile.getBytes();
            logger.info("bytes.length: " + bytes.length);

            // Store the audio recording in the database
            Audio audio = new Audio();
            audio.setTimeLastUpdate(Calendar.getInstance());
            audio.setContentType(contentType);
            audio.setWord(word);
            audio.setTitle(word.getText().toLowerCase());
            audio.setTranscription(word.getText().toLowerCase());
            audio.setBytes(bytes);
            audio.setAudioFormat(audioFormat);
            audioDao.create(audio);
            
            AudioContributionEvent audioContributionEvent = new AudioContributionEvent();
            audioContributionEvent.setContributor(contributor);
            audioContributionEvent.setTime(Calendar.getInstance());
            audioContributionEvent.setAudio(audio);
            audioContributionEvent.setRevisionNumber(audio.getRevisionNumber());
            audioContributionEventDao.create(audioContributionEvent);
        } catch (Exception ex) {
            logger.error(ex);
            
            jsonObject.put("result", "error");
            jsonObject.put("errorMessage", ex.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        
        String jsonResponse = jsonObject.toString();
        logger.info("jsonResponse: " + jsonResponse);
        return jsonResponse;
    }
}
