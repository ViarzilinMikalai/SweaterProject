package viarzilin;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DumpPasswordEncoderTest {
    @Test
    void encode() {
        DumpPasswordEncoder dumpPasswordEncoder = new DumpPasswordEncoder();
        String result = dumpPasswordEncoder.encode("mypwd");
        assertThat(result)
                .isEqualTo("secret: 'mypwd'")
                .contains("secret:")
                .contains("mypwd");
    }

    @Test
    void encodeWithEmptyPassword() {
        DumpPasswordEncoder dumpPasswordEncoder = new DumpPasswordEncoder();
        String result = dumpPasswordEncoder.encode("");
        assertThat(result).isEqualTo("secret: ''");
    }

    @Test
    void encodeWithNullPassword() {
        DumpPasswordEncoder dumpPasswordEncoder = new DumpPasswordEncoder();
        String result = dumpPasswordEncoder.encode(null);
        assertThat(result).isEqualTo("secret: 'null'");
    }

    @Test
    void matchesAlwaysReturnsFalse() {
        DumpPasswordEncoder dumpPasswordEncoder = new DumpPasswordEncoder();
        boolean result = dumpPasswordEncoder.matches("password", "encoded");
        assertThat(result).isFalse();
    }

    @Test
    void matchesWithNullValues() {
        DumpPasswordEncoder dumpPasswordEncoder = new DumpPasswordEncoder();
        boolean result = dumpPasswordEncoder.matches(null, null);
        assertThat(result).isFalse();
    }
}