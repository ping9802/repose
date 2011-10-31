package com.rackspace.papi.components.translation.preprocessor;

public class PreProcessorException extends RuntimeException {
   public PreProcessorException(String message) {
      super(message);
   }
   
   public PreProcessorException(Throwable cause) {
      super(cause);
   }
   
   public PreProcessorException(String message, Throwable cause) {
      super(message, cause);
   }
   
}
