package kg.infosystems.statefin.exception;

public class DecisionFinalStateException extends RuntimeException {
    
    public DecisionFinalStateException(String message) {
        super(message);
    }
    
    public DecisionFinalStateException(String message, Throwable cause) {
        super(message, cause);
    }
}