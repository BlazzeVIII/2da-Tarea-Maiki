package com.supermercado.exception;
import com.supermercado.dto.ApiError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(RecursoNoEncontradoException.class) public ResponseEntity<ApiError> notFound(RecursoNoEncontradoException e){return response(HttpStatus.NOT_FOUND,e.getMessage(),Map.of());}
 @ExceptionHandler(ConflictoException.class) public ResponseEntity<ApiError> conflict(ConflictoException e){return response(HttpStatus.CONFLICT,e.getMessage(),Map.of());}
 @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<ApiError> invalid(MethodArgumentNotValidException e){Map<String,String> d=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->d.put(x.getField(),x.getDefaultMessage()));return response(HttpStatus.BAD_REQUEST,"Revisa los datos enviados.",d);}
 @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<ApiError> integrity(DataIntegrityViolationException e){return response(HttpStatus.CONFLICT,"El dato ya existe o está relacionado con otro recurso.",Map.of());}
 private ResponseEntity<ApiError> response(HttpStatus s,String m,Map<String,String>d){return ResponseEntity.status(s).body(new ApiError(LocalDateTime.now(),s.value(),s.getReasonPhrase(),m,d));}
}
