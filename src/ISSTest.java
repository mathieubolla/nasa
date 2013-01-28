import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ISSTest {
	@Test
	public void shouldRoundDoublesCorrectly() {
		assertThat(ISS.roundedDouble(10.2)).isEqualTo("10.200");
		assertThat(ISS.roundedDouble(10.1234)).isEqualTo("10.123");
		assertThat(ISS.roundedDouble(10.9876)).isEqualTo("10.987");
		assertThat(ISS.roundedDouble(360.0)).isEqualTo("360.000");
		assertThat(ISS.roundedDouble(0.001)).isEqualTo("0.001");
		assertThat(ISS.roundedDouble(-0.001)).isEqualTo("-0.001");
		assertThat(ISS.roundedDouble(-500.0)).isEqualTo("-500.000");
	}

	@Test
	public void shouldLimitAngularPositions() {
		assertThat(ISS.limitAngularPosition(361.0)).isEqualTo(1.0);
		assertThat(ISS.limitAngularPosition(2*360+1.0)).isEqualTo(1.0);
		assertThat(ISS.limitAngularPosition(-1.0)).isEqualTo(359.0);
		assertThat(ISS.limitAngularPosition(-354.0)).isEqualTo(6.0);
		assertThat(ISS.limitAngularPosition(360.0)).isEqualTo(0.0);
	}

	@Test
	public void fullOfStopPlanningShouldNotMove() {

	}
}
