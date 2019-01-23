package com.akucheruk.xml.reader.exception;

public class XmlReaderException extends RuntimeException {

    public XmlReaderException() {
        super();
    }

    public XmlReaderException(String message) {
        super(message);
    }

    public XmlReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlReaderException(Throwable cause) {
        super(cause);
    }
}
