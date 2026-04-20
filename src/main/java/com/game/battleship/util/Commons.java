package com.game.battleship.util;

public class Commons {
    private Commons() {
        // Private constructor to prevent instantiation
    }

    public static final int BOARD_SIZE = 10;
    public static final int QR_CODED_SIZE = 200;
    public static final int MAX_LOOP_ITERATIONS = 1000;

    public static class PageViews {
        public static final String REDIRECT_TO_GAME_VIEW = "redirect:game";
        public static final String GAME_VIEW = "game";
        public static final String HOME_VIEW = "index";
        public static final String MAIN_VIEW = "redirect:/";
    }

    public static class ErrorCodes {
        public static final int VALIDATION_ERROR = 10;
        public static final int PAGE_NOT_FOUND_ERROR = 20;
        public static final int PAGE_UNKNOWN_ERROR = 30;
        public static final int INVALID_SESSION_ERROR = 40;
    }

}
