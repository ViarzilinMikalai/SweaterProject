package viarzilin;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class DumpPasswordEncoderTest {

    @Test
    public void encode() {
        DumpPasswordEncoder dumpPasswordEncoder = new DumpPasswordEncoder();
        Assert.assertEquals("secret: 'mypwd'", dumpPasswordEncoder.encode("mypwd"));
        Assert.assertThat(dumpPasswordEncoder.encode("mypwd"), containsString("mypwd"));
    }
}