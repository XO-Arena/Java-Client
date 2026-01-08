package enums;

public enum PlayerType {
    LOCAL,
    COMPUTER,
    ONLINE;
    
    public SessionType getSessionType() {
        switch(this) {
            case LOCAL:
                return SessionType.LOCAL;
            case COMPUTER:
                return SessionType.AI;
            case ONLINE:
                return SessionType.ONLINE;
        }
        return null;
    }
}
