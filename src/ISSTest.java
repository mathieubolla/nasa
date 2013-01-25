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
	public void shouldComputeAngles() {
		assertThat(ISS.angle(10, 10)).isEqualTo(0.0);
		assertThat(ISS.angle(10, 20)).isEqualTo(10.0);
		assertThat(ISS.angle(20, 10)).isEqualTo(-10.0);
		assertThat(ISS.angle(355, 5)).isEqualTo(10.0);
		assertThat(ISS.angle(5, 355)).isEqualTo(-10.0);
	}

	@Test
	public void shouldTransition() {
		assertThat(ISS.transition(0.0, 1.0, Double.POSITIVE_INFINITY)).isEqualTo(1.0);
		assertThat(ISS.transition(1.0, 0.0, Double.POSITIVE_INFINITY)).isEqualTo(0.0);
		assertThat(ISS.transition(359.0, 0.0, Double.POSITIVE_INFINITY)).isEqualTo(0.0);
		assertThat(ISS.transition(0.0, 359.0, Double.POSITIVE_INFINITY)).isEqualTo(359.0);

		assertThat(ISS.transition(0.0, 1.0, 0.5)).isEqualTo(0.5);
		assertThat(ISS.transition(1.0, 0.0, 0.5)).isEqualTo(0.5);
		assertThat(ISS.transition(359.0, 0.0, 0.5)).isEqualTo(359.5);
		assertThat(ISS.transition(0.0, 359.0, 0.5)).isEqualTo(359.5);
	}

	@Test
	public void shouldSpeed() {
		assertThat(ISS.speed(0.0, 1.0, Double.POSITIVE_INFINITY)).isEqualTo(1.0 / 60);
		assertThat(ISS.speed(1.0, 0.0, Double.POSITIVE_INFINITY)).isEqualTo(-1.0 / 60);
		assertThat(ISS.speed(359.0, 0.0, Double.POSITIVE_INFINITY)).isEqualTo(1.0 / 60);
		assertThat(ISS.speed(0.0, 359.0, Double.POSITIVE_INFINITY)).isEqualTo(-1.0 / 60);

		assertThat(ISS.speed(0.0, 100.0, 0.5)).isEqualTo(0.5);
		assertThat(ISS.speed(100.0, 0.0, 0.5)).isEqualTo(-0.5);
		assertThat(ISS.speed(300.0, 0.0, 0.5)).isEqualTo(0.5);
		assertThat(ISS.speed(0.0, 300.0, 0.5)).isEqualTo(-0.5);
	}

	@Test
	public void shouldHandleSARJCommands() {
		assertThat(ISS.SARJCommand.FRONT.getSpeed(0.0)).isEqualTo(ISS.SARJ_VELOCITY_LIMIT);
		assertThat(ISS.SARJCommand.BACK.getSpeed(0.0)).isEqualTo(-ISS.SARJ_VELOCITY_LIMIT);

		assertThat(ISS.SARJCommand.FRONT.getSpeed(ISS.SARJ_VELOCITY_LIMIT)).isEqualTo(ISS.SARJ_VELOCITY_LIMIT);
		assertThat(ISS.SARJCommand.BACK.getSpeed(ISS.SARJ_VELOCITY_LIMIT)).isEqualTo(-ISS.SARJ_VELOCITY_LIMIT);
	}
}
