package dto;

import enums.InvitationStatus;

/**
 *
 * @author Ahmed_El_Sayyad
 */
public class InvitationDTO {

    private String senderUsername;
    private String receiverUsername;
    private InvitationStatus status;

    public InvitationDTO() {
    }

    public InvitationDTO(String sender, String receiver, InvitationStatus status) {
        this.senderUsername = sender;
        this.receiverUsername = receiver;
        this.status = status;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }
}
