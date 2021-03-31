package app.enums;

public enum NotificationModeEnum {

	MESSAGE("Text Message"),
	WHATSAPP("Whatsapp"),
	EMAIL("email"),
	NATIVE("native notification");
	
	public final String label;

    private NotificationModeEnum(String label) {
        this.label = label;
    }
}
