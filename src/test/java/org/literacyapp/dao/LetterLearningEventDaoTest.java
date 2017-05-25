package org.literacyapp.dao;

import java.util.List;
import org.apache.log4j.Logger;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.literacyapp.model.Device;
import org.literacyapp.model.Student;
import org.literacyapp.model.admin.Application;
import org.literacyapp.model.analytics.LetterLearningEvent;
import org.literacyapp.model.content.Letter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/applicationContext-jpa.xml")
public class LetterLearningEventDaoTest {
    
    private Logger logger = Logger.getLogger(getClass());
    
    @Autowired
    private LetterLearningEventDao letterLearningEventDao;
    
    @Autowired
    private LetterDao letterDao;
    
    @Autowired
    private DeviceDao deviceDao;
    
    @Autowired
    private ApplicationDao applicationDao;
    
    @Autowired
    private StudentDao studentDao;
    
    @Test
    public void testReadAllByDevice() {
        Device device = new Device();
        device.setDeviceId("ddhe72a08347cd29bd3f43d");
        deviceDao.create(device);
        
        List<LetterLearningEvent> letterLearningEvents = letterLearningEventDao.readAll(device);
        assertThat(letterLearningEvents.size(), is(0));
        
        Letter letter = new Letter();
        letter.setText("a");
        letterDao.create(letter);
        
        LetterLearningEvent letterLearningEvent = new LetterLearningEvent();
        letterLearningEvent.setDevice(device);
        letterLearningEvent.setLetter(letter);
        letterLearningEventDao.create(letterLearningEvent);
        
        letterLearningEvents = letterLearningEventDao.readAll(device);
        assertThat(letterLearningEvents.size(), is(1));
        assertThat(letterLearningEvents.get(0).getLetter().getText(), is("a"));
    }
    
    @Test
    public void testReadAllByApplication() {
        Application application = new Application();
        application.setPackageName("org.literacyapp");
        applicationDao.create(application);
        
        List<LetterLearningEvent> letterLearningEvents = letterLearningEventDao.readAll(application);
        assertThat(letterLearningEvents.size(), is(0));
        
        LetterLearningEvent letterLearningEvent = new LetterLearningEvent();
        letterLearningEvent.setApplication(application);
        letterLearningEventDao.create(letterLearningEvent);
        
        letterLearningEvents = letterLearningEventDao.readAll(application);
        assertThat(letterLearningEvents.size(), is(1));
        assertThat(letterLearningEvents.get(0).getApplication().getPackageName(), is("org.literacyapp"));
    }
    
    @Test
    public void testReadAllByStudent() {
        Student student = new Student();
        student.setUniqueId("ddhe72a08347cd29bd3f43d_1");
        studentDao.create(student);
        
        List<LetterLearningEvent> letterLearningEvents = letterLearningEventDao.readAll(student);
        assertThat(letterLearningEvents.size(), is(0));
        
        LetterLearningEvent letterLearningEvent = new LetterLearningEvent();
        letterLearningEvent.setStudent(student);
        letterLearningEventDao.create(letterLearningEvent);
        
        letterLearningEvents = letterLearningEventDao.readAll(student);
        assertThat(letterLearningEvents.size(), is(1));
        assertThat(letterLearningEvents.get(0).getStudent().getUniqueId(), is("ddhe72a08347cd29bd3f43d_1"));
    }
}
