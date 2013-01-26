import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ISS {
	// SSARJ
	// PSARJ
	// 1A
	// 2A
	// 3A
	// 4A
	// 1B (couplage 3A)
	// 2B
	// 3B (couplage 1A)
	// 4B

	public static final double REVERSE = 180.0;
	public static final double ELEVATION = 15.0;

	// SARJ90 => 11min
	// BGA30 => 3min

	//    0   23   46   69
	// 11.5 34.5 57.5 80.5
	// 2A 4A 1B 3B

	private static Simulation makeNegativeSimulation(double skew) {
		return new Simulation(
			new Planning(0.0, 0.0).add(6, Sequence.STOPPED).add(Sequence.SARJ_F90).add(12, Sequence.STOPPED).add(Sequence.SARJ_F90).add(12, Sequence.STOPPED).add(Sequence.SARJ_B90).add(12, Sequence.STOPPED).add(Sequence.SARJ_B90).fillWith(Sequence.STOPPED),
			new Planning(0.0, 0.0).add(6, Sequence.STOPPED).add(Sequence.SARJ_B90).add(12, Sequence.STOPPED).add(Sequence.SARJ_B90).add(12, Sequence.STOPPED).add(Sequence.SARJ_F90).add(12, Sequence.STOPPED).add(Sequence.SARJ_F90).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE - ELEVATION), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_F30).add(20, Sequence.STOPPED).add(Sequence.BGA_B30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(ELEVATION + skew), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_B30).add(20, Sequence.STOPPED).add(Sequence.BGA_F30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE + ELEVATION), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_B30).add(20, Sequence.STOPPED).add(Sequence.BGA_F30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(-ELEVATION + skew), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_F30).add(20, Sequence.STOPPED).add(Sequence.BGA_B30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE + ELEVATION + skew), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_B30).add(20, Sequence.STOPPED).add(Sequence.BGA_F30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(-ELEVATION), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_F30).add(20, Sequence.STOPPED).add(Sequence.BGA_B30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE - ELEVATION + skew), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_F30).add(20, Sequence.STOPPED).add(Sequence.BGA_B30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(ELEVATION), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_B30).add(20, Sequence.STOPPED).add(Sequence.BGA_F30).fillWith(Sequence.STOPPED)
		);
	}

	private static Simulation makePositiveSimulation(double skew) {
		return new Simulation(
			new Planning(0.0, 0.0).add(6, Sequence.STOPPED).add(Sequence.SARJ_F90).add(12, Sequence.STOPPED).add(Sequence.SARJ_F90).add(12, Sequence.STOPPED).add(Sequence.SARJ_B90).add(12, Sequence.STOPPED).add(Sequence.SARJ_B90).fillWith(Sequence.STOPPED),
			new Planning(0.0, 0.0).add(6, Sequence.STOPPED).add(Sequence.SARJ_B90).add(12, Sequence.STOPPED).add(Sequence.SARJ_B90).add(12, Sequence.STOPPED).add(Sequence.SARJ_F90).add(12, Sequence.STOPPED).add(Sequence.SARJ_F90).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(ELEVATION + skew), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_B30).add(20, Sequence.STOPPED).add(Sequence.BGA_F30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE - ELEVATION), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_F30).add(20, Sequence.STOPPED).add(Sequence.BGA_B30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(-ELEVATION + skew), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_F30).add(20, Sequence.STOPPED).add(Sequence.BGA_B30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE + ELEVATION), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_B30).add(20, Sequence.STOPPED).add(Sequence.BGA_F30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(-ELEVATION), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_F30).add(20, Sequence.STOPPED).add(Sequence.BGA_B30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE + ELEVATION + skew), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_B30).add(20, Sequence.STOPPED).add(Sequence.BGA_F30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(ELEVATION), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_B30).add(20, Sequence.STOPPED).add(Sequence.BGA_F30).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE - ELEVATION + skew), 0.0).add(56, Sequence.STOPPED).add(Sequence.BGA_F30).add(20, Sequence.STOPPED).add(Sequence.BGA_B30).fillWith(Sequence.STOPPED)
		);
	}

	private Simulation simulation;

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

	public double getInitialOrientation(double beta) {
		double skew = -((Math.abs(beta) - 70) * 6);
		if (beta < 0) {
			simulation = makeNegativeSimulation(skew);
		} else {
			simulation = makePositiveSimulation(skew);
		}
		return 0.0;
	}

	public double[] getStateAtMinute(int minute) {
		return simulation.getFor(minute);
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

	public static double angle(double a, double b) {
		double delta = limitAngularPosition(b - a);

		if (delta > 180) {
			return delta - 360;
		}

		return delta;
	}

	public static class Command {
		private final double position;
		private final double speed;

		public Command(double position, double speed) {
			this.position = position;
			this.speed = speed;
		}

		public double getPosition() {
			return position;
		}

		public double getSpeed() {
			return speed;
		}
	}

	public static class Sequence {
		private final List<Command> commands;

		public Sequence(Command... commands) {
			this.commands = new ArrayList<Command>(commands.length);
			Collections.addAll(this.commands, commands);
		}

		public long getSize() {
			return commands.size();
		}

		public double[][] getSteps(double originalPosition, double originalSpeed) {
			double[][] steps = new double[(int)getSize()][2];
			double position = originalPosition;
			double speed = originalSpeed;
			for (int i = 0; i < getSize(); i++) {
				Command command = commands.get(i);
				position = limitAngularPosition(position + command.getPosition());
				speed = command.getSpeed();

				steps[i][0] = position;
				steps[i][1] = speed;
			}
			return steps;
		}

		public static final Sequence SARJ_F30 = new Sequence(SARJ.STOP_TO_LFULL(Direction.FRONT), SARJ.HOLDL(Direction.FRONT), SARJ.HOLDL(Direction.FRONT), SARJ.HOLDL(Direction.FRONT), SARJ.FULLL_TO_STOP(Direction.FRONT));

		public static final Sequence SARJ_F90 = new Sequence(
			SARJ.STOP_TO_LFULL(Direction.FRONT),
			SARJ.HOLD(Direction.FRONT),
			SARJ.HOLD(Direction.FRONT),
			SARJ.HOLD(Direction.FRONT),
			SARJ.HOLD(Direction.FRONT),
			SARJ.HOLD(Direction.FRONT),
			SARJ.HOLD(Direction.FRONT),
			SARJ.HOLD(Direction.FRONT),
			SARJ.HOLD(Direction.FRONT),
			SARJ.HOLD(Direction.FRONT),
			SARJ.FULLL_TO_STOP(Direction.FRONT));

		public static final Sequence SARJ_B30 = new Sequence(SARJ.STOP_TO_LFULL(Direction.BACK), SARJ.HOLDL(Direction.BACK), SARJ.HOLDL(Direction.BACK), SARJ.HOLDL(Direction.BACK), SARJ.FULLL_TO_STOP(Direction.BACK));

		public static final Sequence SARJ_B90 = new Sequence(
			SARJ.STOP_TO_LFULL(Direction.BACK),
			SARJ.HOLD(Direction.BACK),
			SARJ.HOLD(Direction.BACK),
			SARJ.HOLD(Direction.BACK),
			SARJ.HOLD(Direction.BACK),
			SARJ.HOLD(Direction.BACK),
			SARJ.HOLD(Direction.BACK),
			SARJ.HOLD(Direction.BACK),
			SARJ.HOLD(Direction.BACK),
			SARJ.HOLD(Direction.BACK),
			SARJ.FULLL_TO_STOP(Direction.BACK));

		public static final Sequence BGA_F15 = new Sequence(BGA.STOP_TO_FULL(Direction.FRONT), BGA.FULL_TO_STOP(Direction.FRONT));
		public static final Sequence BGA_F30 = new Sequence(BGA.STOP_TO_FULL(Direction.FRONT), BGA.HOLD(Direction.FRONT), BGA.FULL_TO_STOP(Direction.FRONT));
		public static final Sequence BGA_F45 = new Sequence(BGA.STOP_TO_FULL(Direction.FRONT), BGA.HOLD(Direction.FRONT), BGA.HOLD(Direction.FRONT), BGA.FULL_TO_STOP(Direction.FRONT));

		public static final Sequence BGA_B15 = new Sequence(BGA.STOP_TO_FULL(Direction.BACK), BGA.FULL_TO_STOP(Direction.BACK));
		public static final Sequence BGA_B30 = new Sequence(BGA.STOP_TO_FULL(Direction.BACK), BGA.HOLD(Direction.BACK), BGA.FULL_TO_STOP(Direction.BACK));
		public static final Sequence BGA_B45 = new Sequence(BGA.STOP_TO_FULL(Direction.BACK), BGA.HOLD(Direction.BACK), BGA.HOLD(Direction.BACK), BGA.FULL_TO_STOP(Direction.BACK));

		public static final Sequence STOPPED = new Sequence(All.STOPPED);

		public String toString() {
			StringBuilder builder = new StringBuilder("Sequence:");
			for (double[] step : getSteps(0.0, 0.0)) {
				builder.append("step:");
				for (double stepPart : step) {
					builder.append(roundedDouble(stepPart)).append(" ");
				}
				builder.append("\n");
			}
			return builder.toString();
		}
	}

	public static class Simulation {
		private final double[][] plannings;

		public Simulation(Planning... plannings) {
			if (plannings.length != 10) {
				throw new IllegalArgumentException("We have 10 motors and simulation contains only "+plannings.length+" plannings");
			}
			this.plannings = new double[92][20];

			int motor = 0;
			for (Planning planning: plannings) {
				int minute = 0;
				for (double[] step : planning.getSteps()) {
					this.plannings[minute][motor] = step[0]; // velocity
					this.plannings[minute][motor+1] = step[1]; // speed
					minute++;
				}
				motor += 2;
			}
		}

		public double[] getFor(int minute) {
			return plannings[minute];
		}
	}

	public static class Planning {
		private final List<Sequence> planning;
		private final double initialPosition;
		private final double initialSpeed;

		public Planning(double initialPosition, double initialSpeed) {
			this.planning = new ArrayList<Sequence>();
			this.initialPosition = initialPosition;
			this.initialSpeed = initialSpeed;
		}

		public Planning add(Sequence sequence) {
			planning.add(sequence);
			return this;
		}

		public Planning add(int repeat, Sequence sequence) {
			for (int i = 0; i < repeat; i++) {
				planning.add(sequence);
			}
			return this;
		}

		public Planning fillWith(Sequence sequence) {
			while (!check()) {
				add(sequence);
			}
			return this;
		}

		public double[][] getSteps() {
			check();
			double[][] steps = new double[92][2];
			double currentPosition = initialPosition;
			double currentSpeed = initialSpeed;

			int i = 0;
			for (Sequence sequence : planning) {
				for (double[] step : sequence.getSteps(currentPosition, currentSpeed)) {
					steps[i++] = step;
					currentPosition = step[0];
					currentSpeed = step[1];
				}
			}
			return steps;
		}

		public boolean check() {
			long size = 0;
			for (Sequence sequence : planning) {
				size += sequence.getSize();
			}
			if (size > 92) {
				throw new IllegalStateException("You have an oversize planning");
			}

			return size == 92;
		}

		public String toString() {
			StringBuilder builder = new StringBuilder("BGA_1:");
			for (double[] step : getSteps()) {
				builder.append("step:");
				for (double stepPart : step) {
					builder.append(roundedDouble(stepPart)).append(" ");
				}
				builder.append("\n");
			}
			return builder.toString();
		}
	}

	public enum Direction {
		FRONT(1.0), BACK(-1.0);

		private final double signum;

		Direction(double signum) {
			this.signum = signum;
		}

		public Command applyTo(Command command) {
			return new Command(signum * command.getPosition(), signum * command.getSpeed());
		}
	}

	public static class All {
		public static final Command STOPPED = new Command(0.0, 0.0);
	}

	public static class BGA {
		public static Command STOP_TO_FULL(Direction direction) {
			return direction.applyTo(new Command(11.875, 0.25));
		}

		public static Command HOLD(Direction direction) {
			return direction.applyTo(new Command(15, 0.25));
		}

		public static Command FULL_TO_STOP(Direction direction) {
			return direction.applyTo(new Command(3.125, 0.0));
		}
	}

	public static class SARJ {
		public static Command STOP_TO_LFULL(Direction direction) {
			return direction.applyTo(new Command(6.75, 0.15));
		}

		public static Command HOLDL(Direction direction) {
			return direction.applyTo(new Command(7, 0.15));
		}

		public static Command HOLD(Direction direction) {
			return direction.applyTo(new Command(9, 0.15));
		}

		public static Command FULLL_TO_STOP(Direction direction) {
			return direction.applyTo(new Command(2.25, 0.0));
		}
	}
}