package app.paper_filler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler
    public String handle(Exception e, Model model){
        log.error("Unrecognized exception",e);
        model.addAttribute("message", "Something went wrong.Try again or ask for maintenance."+ "\n" +"Что-то пошло не так. Попробуйте ещё раз или обратитесь к администратору.");
        //todo add rus and english error message
        return "error";
    }
}
