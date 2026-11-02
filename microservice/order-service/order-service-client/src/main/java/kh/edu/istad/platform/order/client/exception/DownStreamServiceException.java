package kh.edu.istad.platform.order.client.exception;

public class DownStreamServiceException extends RuntimeException {

    private String service;

    public DownStreamServiceException(String message) {
        super(message);
    }

    public DownStreamServiceException(String service, String message) {
        super(message);
        this.service = service;
    }


    public DownStreamServiceException(String message, String service, Throwable cause) {
        super(message, cause);
        this.service = service;
    }

    public String getService() {
        return service;
    }




}