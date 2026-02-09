package com.revplay.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    
    UserServiceTest.class,
    UserDAOTest.class,
    MusicPlayerServiceTest.class,
    PlaylistDAOTest.class
    // PlaylistDAOTest.class  // optional
})
public class AllTests {
    // nothing here
}
