package app.enums;

public enum SupportTicketStatus {

	OPEN ("Open"), INPROGRESS("IN_Progress"), CLOSED("Closed");
	
	public final String label;

    private SupportTicketStatus(String label) {
        this.label = label;
    }
}
