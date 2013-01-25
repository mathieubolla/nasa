import java.io.*;

public class ISS {
	public static final double SARJ_VELOCITY_LIMIT = 0.150 / 4; // per second
	public static final double SARJ_VELOCITY_HALF = 0.075 / 4; // per second

	public static final double BGA_VELOCITY_LIMIT = 0.0 / 8; // per second
	public static final double BGA_VELOCITY_HALF = 0.0 / 8; // per second

	public static final Positions INITIAL_POSITION = new Positions(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	public static final Speeds MINUMUM_SPEED = new Speeds(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

	public static void main(String... args) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		ISS iss = new ISS();

		String beta = in.readLine();
		System.out.println(iss.getInitialOrientation(Double.parseDouble(beta)));
		System.out.flush();

		for (int minute = 0; minute <= 91; minute++) {
			System.out.println("1");
			for (double angle : iss.getStateAtMinute(minute)) {
				System.out.println(roundedDouble(angle));
			}
			System.out.flush();
		}
	}

	private Status status = new Status(INITIAL_POSITION, MINUMUM_SPEED);

	public double getInitialOrientation(double beta) {
		return 3.0;
	}

	public double[] getStateAtMinute(int minute) {
		Status nextStatus = null;
		if (minute >= 0) {
			nextStatus = status.computeNextFromCommandVector(
				SARJCommand.FRONT_HALF, SARJCommand.FRONT_HALF,
				BGACommand.FRONT_HALF, BGACommand.FRONT_HALF,
				BGACommand.FRONT_HALF, BGACommand.FRONT_HALF,
				BGACommand.FRONT_HALF, BGACommand.FRONT_HALF,
				BGACommand.FRONT_HALF, BGACommand.FRONT_HALF);
		}
		if (minute > 20) {
			nextStatus = status.computeNextFromPositionVector(
				0.0, 0.0,
				0.0, 0.0,
				0.0, 0.0,
				0.0, 0.0,
				0.0, 0.0
			);
		}

		double[] retour = status.getStatus();

		status = nextStatus;

		return retour;
	}

	public static class Speeds {
		private final double[] speeds;

		public Speeds(double... values) {
			this.speeds = values;
		}

		public String toString() {
			StringBuilder toString = new StringBuilder();
			for (double value : speeds) {
				toString.append(roundedDouble(value)).append(" ");
			}
			return toString.toString();
		}
	}

	public static class Positions {
		private final double[] positions;

		private Positions(Positions previousPosition, Speeds previousSpeeds, SARJCommand sarjA, SARJCommand sarjB, BGACommand... bgaCommands) {
			this(new double[10]);

			positions[0] = previousPosition.positions[0] + sarjA.getSpeed(previousSpeeds.speeds[0]) * 60;
			positions[1] = previousPosition.positions[1] + sarjB.getSpeed(previousSpeeds.speeds[1]) * 60;

			for (int i = 2; i < 10; i++) {
				positions[i] = limitAngularPosition((previousPosition.positions[i] + bgaCommands[i-2].getSpeed(previousSpeeds.speeds[i-2])) * 60);
			}
		}

		public Positions(double... values) {
			this.positions = values;
		}

		private Speeds computeSpeedsForNew(Positions newPosition) {
			double[] resultingValues = new double[10];
			for (int i = 0; i < 2; i++) {
				resultingValues[i] = speed(positions[i], newPosition.positions[i], SARJ_VELOCITY_LIMIT);
			}
			for (int i = 2; i < 10; i++) {
				resultingValues[i] = speed(positions[i], newPosition.positions[i], BGA_VELOCITY_LIMIT);
			}
			return new Speeds(resultingValues);
		}


		public String toString() {
			StringBuilder toString = new StringBuilder();
			for (double value : positions) {
				toString.append(roundedDouble(value)).append(" ");
			}
			return toString.toString();
		}
	}

	public static class Status {
		private final Positions positions;
		private final Speeds speeds;

		public Status(Positions initialPositions, Speeds initialSpeeds) {
			this.positions = initialPositions;
			this.speeds = initialSpeeds;
		}

		public Status computeNextFromCommandVector(SARJCommand sarjA, SARJCommand sarjB, BGACommand... bgaCommands) {
			Positions nextPositions = new Positions(positions, speeds, sarjA, sarjB, bgaCommands);
			Speeds nextSpeeds = positions.computeSpeedsForNew(nextPositions);

			return new Status(nextPositions, nextSpeeds);
		}

		public Status computeNextFromPositionVector(double... angularPositions) {
			Positions nextPositions = new Positions(
				transition(positions.positions[0], angularPositions[0], SARJ_VELOCITY_LIMIT * 60),
				transition(positions.positions[1], angularPositions[1], SARJ_VELOCITY_LIMIT * 60),
				transition(positions.positions[2], angularPositions[2], BGA_VELOCITY_LIMIT * 60),
				transition(positions.positions[3], angularPositions[3], BGA_VELOCITY_LIMIT * 60),
				transition(positions.positions[4], angularPositions[4], BGA_VELOCITY_LIMIT * 60),
				transition(positions.positions[5], angularPositions[5], BGA_VELOCITY_LIMIT * 60),
				transition(positions.positions[6], angularPositions[6], BGA_VELOCITY_LIMIT * 60),
				transition(positions.positions[7], angularPositions[7], BGA_VELOCITY_LIMIT * 60),
				transition(positions.positions[8], angularPositions[8], BGA_VELOCITY_LIMIT * 60),
				transition(positions.positions[9], angularPositions[9], BGA_VELOCITY_LIMIT * 60)
			);
			Speeds nextSpeeds = positions.computeSpeedsForNew(nextPositions);

			return new Status(nextPositions, nextSpeeds);
		}

		public double[] getStatus() {
			return new double[] {
				positions.positions[0],
				speeds.speeds[0],
				positions.positions[1],
				speeds.speeds[1],
				positions.positions[2],
				speeds.speeds[2],
				positions.positions[3],
				speeds.speeds[3],
				positions.positions[4],
				speeds.speeds[4],
				positions.positions[5],
				speeds.speeds[5],
				positions.positions[6],
				speeds.speeds[6],
				positions.positions[7],
				speeds.speeds[7],
				positions.positions[8],
				speeds.speeds[8],
				positions.positions[9],
				speeds.speeds[9]
			};
		}

		public String toString() {
			return "Positions:"+positions+", Speeds:"+speeds;
		}
	}

	public static enum SARJCommand {
		FRONT(SARJ_VELOCITY_LIMIT), FRONT_HALF(SARJ_VELOCITY_HALF), STOP(0.0), BACK_HALF(-SARJ_VELOCITY_HALF), BACK(-SARJ_VELOCITY_LIMIT);

		private final double commandSpeed;

		SARJCommand(double commandSpeed) {
			this.commandSpeed = commandSpeed;
		}

		public double getSpeed(double previousSpeed) {
			return commandSpeed;
		}
	}

	public static enum BGACommand {
		FRONT(BGA_VELOCITY_LIMIT), FRONT_HALF(BGA_VELOCITY_HALF), STOP(0.0), BACK_HALF(-BGA_VELOCITY_HALF), BACK(-BGA_VELOCITY_LIMIT);

		private final double commandSpeed;

		BGACommand(double commandSpeed) {
			this.commandSpeed = commandSpeed;
		}

		public double getSpeed(double previousSpeed) {
			return commandSpeed;
		}
	}

	public static String roundedDouble(double inputValue) {
		double value = Math.abs(inputValue);
		double signum = Math.signum(inputValue);

		long left = (long) value;
		long right = Math.round(100000 * (value - left)) / 100;
		String padding = "";
		if (right < 10) {
			padding += "0";
		}
		if (right < 100) {
			padding += "0";
		}
		return (signum < 0.0 ? "-" : "") + left + "." + padding + right;
	}

	public static double limitAngularPosition(double value) {
		double modulo = value % 360;
		return modulo >= 0.0 ? modulo : 360 + modulo;
	}

	public static double transition(double from, double to, double maximumSpeed) {
		from = limitAngularPosition(from);
		to = limitAngularPosition(to);

		double delta = angle(from, to);
		double signum = Math.signum(delta);
		double speed = Math.min(maximumSpeed, Math.abs(delta));

		return limitAngularPosition(from + signum * speed);
	}

	public static double speed(double from, double to, double maximumSpeed) {
		from = limitAngularPosition(from);
		to = limitAngularPosition(to);

		double delta = angle(from, to);
		double signum = Math.signum(delta);
		double speed = Math.min(maximumSpeed, Math.abs(delta / 60));

		return signum * speed;
	}

	public static double angle(double a, double b) {
		double delta = limitAngularPosition(b - a);

		if (delta > 180) {
			return delta - 360;
		}

		return delta;
	}
}