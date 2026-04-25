package viarzilin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleTest {

    @Test
    public void test(){
        int x = 2;
        int y = 23;

        assertEquals(46, x * y);
        assertEquals(25, x + y);
    }

    @Test
    public void error(){
        int i = 0;
        int i1 = 1/i;
    }
}
