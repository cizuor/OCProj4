package com.openclassrooms.mddapi.exception;


import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import com.openclassrooms.mddapi.payload.response.MessageResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	
	@ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<MessageResponse> handleNumberFormatException(NumberFormatException ex) {
        return ResponseEntity.badRequest() .body(new MessageResponse("Le format de nombre est invalide."));
    }
	
	@ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<MessageResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(ex.getMessage()));
    }
	
	@ExceptionHandler(AccessDeniedException.class) 
    public ResponseEntity<MessageResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(new MessageResponse(ex.getMessage()));
    }
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGlobal(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new MessageResponse("Une erreur interne est survenue"));
    }
}
