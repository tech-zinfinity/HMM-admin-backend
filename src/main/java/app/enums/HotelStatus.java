package app.enums;

public enum HotelStatus {

	DRAFT ("DRAFT"), APPROVED ("APPROVED"), REJECTED ("REJECTED"), PUBLISHED ("PUBLISHED");
	
	public final String label;

    private HotelStatus(String label) {
        this.label = label;
    }
}
