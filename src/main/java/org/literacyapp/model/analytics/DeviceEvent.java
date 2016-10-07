package org.literacyapp.model.analytics;

import java.util.Calendar;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.literacyapp.model.BaseEntity;
import org.literacyapp.model.Device;

@MappedSuperclass
public abstract class DeviceEvent extends BaseEntity {
    
    @NotNull
    @ManyToOne
    private Device device;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar calendar;

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
