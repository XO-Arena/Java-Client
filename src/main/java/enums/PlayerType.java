package enums;

public enum PlayerType {
    LOCAL,
    COMPUTER,
    RECORDED,
    ONLINE;
    
    public SessionType getSessionType() {
        switch(this) {
            case LOCAL:
                return SessionType.LOCAL;
            case COMPUTER:
                return SessionType.AI;
            case RECORDED:
                return SessionType.RECORDED;
            case ONLINE:
                return SessionType.ONLINE;
        }
        return null;
    }
}
