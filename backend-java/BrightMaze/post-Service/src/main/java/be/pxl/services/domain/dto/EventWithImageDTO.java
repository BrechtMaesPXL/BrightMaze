package be.pxl.services.domain.dto;

public class EventWithImageDTO {
    private EventResponse event;
    private String base64Image;

    public EventResponse getEvent() {
        return event;
    }

    public void setEvent(EventResponse event) {
        this.event = event;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }
}
