package app.enums;

public enum HotelStatus {

	REQUESTED ("REQUESTED"), APPROVED ("APPROVED"), REJECTED ("REJECTED"),REQUESTEDFORPUBLISH("Requested For Publish"), PUBLISHED ("PUBLISHED"), VERIFIED("VERIFIED");
	
	public final String label;

    private HotelStatus(String label) {
        this.label = label;
    }
}
