package com.akucheruk.xml.reader.exception;

public class XmlReaderFileNotFoundException extends RuntimeException {
    public XmlReaderFileNotFoundException() {
        super();
    }

    public XmlReaderFileNotFoundException(String message) {
        super(message);
    }

    public XmlReaderFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlReaderFileNotFoundException(Throwable cause) {
        super(cause);
    }
}
