package code.with.bingyang.wei.hogwartsartifactsonline.system.exception;

import code.with.bingyang.wei.hogwartsartifactsonline.system.Result;
import code.with.bingyang.wei.hogwartsartifactsonline.system.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static code.with.bingyang.wei.hogwartsartifactsonline.system.StatusCode.*;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({ObjectNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result handleArtifactNotFoundException(ObjectNotFoundException e) {
        return new Result(false, NOT_FOUND, e.getMessage(), null);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleMethodValidationException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        Map<String, String> map = new HashMap<>(errors.size());

        errors.forEach(error -> {
            String key = ((FieldError) error).getField();
            String value = error.getDefaultMessage();
            map.put(key, value);
        });

        return new Result(
                false,
                StatusCode.INVALID_ARGUMENT,
                "Provided arguments are invalid, see data for details.",
                map
        );
    }


    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result handleAuthenticationException(Exception e) {
        return new Result(false, UNAUTHORIZED, "username or password is incorrect.", e.getMessage());
    }


    @ExceptionHandler({AccountStatusException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result handleAccountStatusException(AccountStatusException e) {
        return new Result(false, UNAUTHORIZED, "User account is abnormal.", e.getMessage());
    }


    @ExceptionHandler({InvalidBearerTokenException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result handleInvalidBearerTokenException(InvalidBearerTokenException e) {
        return new Result(false, UNAUTHORIZED, "The access token provided is expired, revoked, malformed, or invalid for other reasons.", e.getMessage());
    }


    @ExceptionHandler({org.springframework.security.access.AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        return new Result(false, FORBIDDEN, "No permission.", e.getMessage());
    }


    /**
     * Fallback handles any unhandled exceptions.
     *
     * @param e exception object
     * @return
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleOtherException(Exception e) {
        return new Result(false, INTERNAL_SERVE_ERROR, "A server internal error occurs.", e.getMessage());
    }

}
