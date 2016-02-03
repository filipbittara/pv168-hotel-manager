package cz.mumi.fi.pv168.backend;

/**
 * Indicates service failure.
 */
public class ServiceFailureException  extends RuntimeException {

    public ServiceFailureException(String msg) {
        super(msg);
    }

    public ServiceFailureException(Throwable cause) {
        super(cause);
    }

    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}