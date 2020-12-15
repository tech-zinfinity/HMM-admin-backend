package app.enums;

public enum HotelStatus {

	REQUESTED ("REQUESTED"), APPROVED ("APPROVED"), REJECTED ("REJECTED"), PUBLISHED ("PUBLISHED"), VERIFIED("VERIFIED");
	
	public final String label;

    private HotelStatus(String label) {
        this.label = label;
    }
}
