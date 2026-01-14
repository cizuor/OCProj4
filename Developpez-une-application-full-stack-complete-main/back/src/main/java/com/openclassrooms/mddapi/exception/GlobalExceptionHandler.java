package com.openclassrooms.mddapi.exception;


import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

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
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<MessageResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
	    String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
	    
	    return ResponseEntity.badRequest()
	                         .body(new MessageResponse("Erreur de validation : " + errorMessage));
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<MessageResponse> handleBadCredentials(BadCredentialsException ex) {
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED) 
	                         .body(new MessageResponse("Email ou mot de passe incorrect"));
	}
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGlobal(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new MessageResponse("Une erreur interne est survenue"));
    }
}
