package student.mgt.exception;

public class BusCapacityExceededException extends RuntimeException {

    public BusCapacityExceededException(String message) {
        super(message);
    }

    public BusCapacityExceededException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}