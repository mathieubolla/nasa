import java.io.*;
import java.util.*;

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
    public static final double FULL_ORBIT = 360.0;
    public static final int ORBIT_DURATION = 92;
    public static final double ANGULAR_SPEED = FULL_ORBIT / ORBIT_DURATION / 60;

    private double elevation;
    private double yaw;
    private double skewFactor;
    private double offsetStart;

    private Map<Double, Double> offsetsStart;
    private Map<Double, Double> elevations;
    private Map<Double, Double> yaws;
    private Map<Double, Double> skewFactors;

    public ISS() {
        this(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public ISS(double elevation, double yaw, double skewFactor, double offsetStart) {
        this.elevation = elevation;
        this.yaw = yaw;
        this.skewFactor = skewFactor;
        this.offsetStart = offsetStart;

        offsetsStart = new HashMap<Double, Double>();
        offsetsStart.put(-75.0, 355.0);
        offsetsStart.put(-74.0, 355.0);
        offsetsStart.put(-73.0, 355.0);
        offsetsStart.put(-72.0, 0.0);
        offsetsStart.put(-71.0, 0.0);
        offsetsStart.put(-70.0, 5.0);
        offsetsStart.put(75.0, 0.0);
        offsetsStart.put(74.0, 0.0);
        offsetsStart.put(73.0, 0.0);
        offsetsStart.put(72.0, 0.0);
        offsetsStart.put(71.0, 0.0);
        offsetsStart.put(70.0, 0.0);

        elevations = new HashMap<Double, Double>();
        elevations.put(-75.0, 22.0);
        elevations.put(-74.0, 21.0);
        elevations.put(-73.0, 20.0);
        elevations.put(-72.0, 19.0);
        elevations.put(-71.0, 19.0);
        elevations.put(-70.0, 19.0);
        elevations.put(75.0, 17.0);
        elevations.put(74.0, 18.0);
        elevations.put(73.0, 18.0);
        elevations.put(72.0, 19.0);
        elevations.put(71.0, 19.0);
        elevations.put(70.0, 19.0);

        yaws = new HashMap<Double, Double>();
        yaws.put(-75.0, 0.0);
        yaws.put(-74.0, 0.0);
        yaws.put(-73.0, 0.0);
        yaws.put(-72.0, 0.0);
        yaws.put(-71.0, 0.0);
        yaws.put(-70.0, 0.0);
        yaws.put(75.0, 0.0);
        yaws.put(74.0, 0.0);
        yaws.put(73.0, 0.0);
        yaws.put(72.0, 0.0);
        yaws.put(71.0, 0.0);
        yaws.put(70.0, 0.0);

        skewFactors = new HashMap<Double, Double>();
        skewFactors.put(-75.0, 6.0);
        skewFactors.put(-74.0, 6.0);
        skewFactors.put(-73.0, 6.0);
        skewFactors.put(-72.0, 0.0);
        skewFactors.put(-71.0, 6.0);
        skewFactors.put(-70.0, 6.0);
        skewFactors.put(75.0, 6.0);
        skewFactors.put(74.0, 6.0);
        skewFactors.put(73.0, 6.0);
        skewFactors.put(72.0, 0.0);
        skewFactors.put(71.0, 0.0);
        skewFactors.put(70.0, 6.0);
    }

    private static Simulation makeNegativeSimulation(double skew, double offsetStart, double elevation) {
        return new Simulation(
                new Planning(offsetStart, ANGULAR_SPEED).add(constant(Direction.FRONT)),
                new Planning(-offsetStart, ANGULAR_SPEED).add(constant(Direction.BACK)),
                new Planning(limitAngularPosition(REVERSE - elevation), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(elevation + skew), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(REVERSE + elevation), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(-elevation - skew), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(REVERSE + elevation + skew), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(-elevation), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(REVERSE - elevation - skew), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(elevation), 0.0).fillWith(Sequence.STOPPED)
        );
	}

    private static Simulation makePositiveSimulation(double skew, double offsetStart, double elevation) {
        return new Simulation(
                new Planning(offsetStart, ANGULAR_SPEED).add(constant(Direction.FRONT)),
                new Planning(-offsetStart, ANGULAR_SPEED).add(constant(Direction.BACK)),
                new Planning(limitAngularPosition(elevation + skew), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(REVERSE - elevation), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(-elevation - skew), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(REVERSE + elevation), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(-elevation), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(REVERSE + elevation + skew), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(elevation), 0.0).fillWith(Sequence.STOPPED),
                new Planning(limitAngularPosition(REVERSE - elevation - skew), 0.0).fillWith(Sequence.STOPPED)
        );
    }

	private Simulation simulation;

    private void setFlightConstants(double beta) {
        elevation = getConstant(elevation, elevations, beta);
        yaw = getConstant(yaw, yaws, beta);
        skewFactor = getConstant(skewFactor, skewFactors, beta);
        offsetStart = getConstant(offsetStart, offsetsStart, beta);
    }

    private double getConstant(double currentValue, Map<Double, Double> source, double beta) {
        if (!Double.isNaN(currentValue)) {
            return currentValue;
        }
        return source.get(beta);
    }

	public static void main(String... args) throws Exception {
        ISS iss;
        if (args.length == 4) {
            iss = new ISS(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
        } else {
            iss = new ISS();
        }

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		String beta = in.readLine();
		System.out.println(iss.getInitialOrientation(Double.parseDouble(beta)));
		System.out.flush();

		for (int minute = 0; minute < ORBIT_DURATION; minute++) {
			System.out.println("1");
			for (double angle : iss.getStateAtMinute(minute)) {
				System.out.println(roundedDouble(angle));
			}
			System.out.flush();
		}
	}

	public double getInitialOrientation(double beta) {
        setFlightConstants(beta);

        double skew = -((Math.abs(beta) - 70) * skewFactor);
		if (beta < 0) {
            simulation = makeNegativeSimulation(skew, offsetStart, elevation);
		} else {
			simulation = makePositiveSimulation(skew, offsetStart, elevation);
		}
        return yaw;
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

		public double[][] getSteps(double originalPosition) {
			double[][] steps = new double[(int)getSize()][2];
			double position = originalPosition;
            for (int i = 0; i < getSize(); i++) {
				Command command = commands.get(i);
				position = limitAngularPosition(position + command.getPosition());

                steps[i][0] = position;
				steps[i][1] = command.getSpeed();
			}
			return steps;
		}

		public static final Sequence STOPPED = new Sequence(All.STOPPED);
	}

	public static class Simulation {
		private final double[][] plannings;

		public Simulation(Planning... plannings) {
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

			int i = 0;
			for (Sequence sequence : planning) {
				for (double[] step : sequence.getSteps(currentPosition)) {
					steps[i++] = step;
					currentPosition = step[0];
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

    public static Sequence constant(Direction direction) {
        Command[] commands = new Command[ORBIT_DURATION];
        for (int i = 0; i < ORBIT_DURATION; i++) {
            commands[i] = direction.applyTo(new Command(ANGULAR_SPEED * 60, ANGULAR_SPEED));
        }

        return new Sequence(commands);
    }
}