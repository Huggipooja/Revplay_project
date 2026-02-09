package com.revplay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.revplay.ui.UserMenu;

public class MainApp {
	
    private static final Logger logger =LogManager.getLogger(MainApp.class);

    public static void main(String[] args) {
    	logger.info("🚀 RevPlay application started");

        UserMenu menu = new UserMenu();
        menu.start();
    }
}
