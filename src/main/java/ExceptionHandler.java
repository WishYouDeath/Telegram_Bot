public class ExceptionHandler {

    public static class EmptyResponseException extends RuntimeException {
        public EmptyResponseException(String message) {
            super(message);
        }
    }
    public static class DataProcessingException extends RuntimeException {
        public DataProcessingException(String message) {
            super(message);
        }
        public DataProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class HttpRequestException extends RuntimeException {
        public HttpRequestException(String message) {
            super(message);
        }

        public HttpRequestException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    public static class DateProcessingException extends RuntimeException {
        public DateProcessingException(String message) {
            super(message);
        }
        public DateProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
