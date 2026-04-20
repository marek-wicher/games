package com.game.battleship.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.game.battleship.util.Commons.PageViews.MAIN_VIEW;
import static com.game.battleship.util.Commons.ErrorCodes.PAGE_NOT_FOUND_ERROR;
import static com.game.battleship.util.Commons.ErrorCodes.PAGE_UNKNOWN_ERROR;
import static com.game.battleship.util.Commons.PageViews.REDIRECT_TO_GAME_VIEW;
import static com.game.battleship.util.Commons.ErrorCodes.VALIDATION_ERROR;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ModelAndView handleValidationException(HttpServletRequest req, HandlerMethodValidationException ex, RedirectAttributes redirectAttributes) {
        log.atError()
                .setMessage("[ERROR ValidationException] {}: {}")
                .addArgument(ex.getClass().getName())
                .addArgument(ex.getMessage())
                .log();
        redirectAttributes.addFlashAttribute("error", VALIDATION_ERROR);
        ModelAndView mav = new ModelAndView();
        mav.setViewName(REDIRECT_TO_GAME_VIEW);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(HttpServletRequest req,
            Exception ex,
            RedirectAttributes redirectAttributes) {
        log.atError()
                .setMessage("[ERROR Exception] {}: {}. Stack trace: {}")
                .addArgument(ex.getClass().getName())
                .addArgument(ex.getMessage())
                .addArgument(() -> {
                    StringBuilder sb = new StringBuilder();
                    for (StackTraceElement element : ex.getStackTrace()) {
                        sb.append(element.toString()).append("\n");
                    }
                    return sb.toString();
                })
                .log();
        ModelAndView mav = new ModelAndView();
        mav.setViewName(MAIN_VIEW);
        redirectAttributes.addFlashAttribute("error", PAGE_UNKNOWN_ERROR);
        return mav;
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(HttpServletRequest req,
            RuntimeException ex,
            RedirectAttributes redirectAttributes) {
        log.atError()
                .setMessage("[ERROR RuntimeException] {}: {}. Stack trace: {}")
                .addArgument(ex.getClass().getName())
                .addArgument(ex.getMessage())
                .addArgument(() -> {
                    StringBuilder sb = new StringBuilder();
                    for (StackTraceElement element : ex.getStackTrace()) {
                        sb.append(element.toString()).append("\n");
                    }
                    return sb.toString();
                })
                .log();
        ModelAndView mav = new ModelAndView();
        mav.setViewName(MAIN_VIEW);
        redirectAttributes.addFlashAttribute("error", PAGE_UNKNOWN_ERROR);
        return mav;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFoundException(HttpServletRequest req,
            NoResourceFoundException ex,
            RedirectAttributes redirectAttributes) {
        log.atError()
                .setMessage("[ERROR NoResourceFoundException] {}: {}. Stack trace: {}")
                .addArgument(ex.getClass().getName())
                .addArgument(ex.getMessage())
                .addArgument(() -> {
                    StringBuilder sb = new StringBuilder();
                    for (StackTraceElement element : ex.getStackTrace()) {
                        sb.append(element.toString()).append("\n");
                    }
                    return sb.toString();
                })
                .log();
        redirectAttributes.addFlashAttribute("error", PAGE_NOT_FOUND_ERROR);
        ModelAndView mav = new ModelAndView();
        mav.setViewName(MAIN_VIEW);
        return mav;
    }
}
