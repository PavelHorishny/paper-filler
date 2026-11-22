package app.paper_filler.teryt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NormalizeTest {

    @Test
    void removesPolishLetters() {
        assertThat(TerytAddressFinder.normalize("Łódź")).isEqualTo("lodz");
        assertThat(TerytAddressFinder.normalize("Jagiellońska")).isEqualTo("jagiellonska");
        assertThat(TerytAddressFinder.normalize("ŻÓŁKIEWSKIEGO")).isEqualTo("zolkiewskiego");
    }

    @Test
    void nullGivesEmpty() {
        assertThat(TerytAddressFinder.normalize(null)).isEmpty();
    }
}
