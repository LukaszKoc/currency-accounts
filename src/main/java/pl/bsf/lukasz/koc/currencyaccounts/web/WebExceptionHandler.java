package pl.bsf.lukasz.koc.currencyaccounts.web;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bsf.lukasz.koc.currencyaccounts.exception.NotFoundException;

@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public Map<String, String> handle404(NotFoundException ex) {
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("errorMessage", ex.getMessage());

		log.debug("Hndled exception with response: 404 {}", errorResponse);
		return errorResponse;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(RuntimeException.class)
	public Map<String, String> handle400(RuntimeException ex) {
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("errorMessage", ex.getMessage());

		log.debug("Hndled exception with response: 400 {}", errorResponse);
		return errorResponse;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		log.debug("Hndled exception with response: 400 {}", errors);
		return errors;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public Map<String, String> handle500(Exception ex) {
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("errorMessage", ex.getMessage());

		log.debug("Hndled exception with response: 500 {}", errorResponse);
		return errorResponse;
	}
}
