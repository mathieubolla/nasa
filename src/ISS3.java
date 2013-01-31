import com.mathieubolla.externalui.ExternalUiProvider;
import com.mathieubolla.externalui.events.*;
import com.mathieubolla.externalui.events.EventListener;
import com.mathieubolla.externalui.uibuilder.Line;

import java.io.*;
import java.util.*;

import static com.mathieubolla.externalui.uibuilder.Input.input;
import static com.mathieubolla.externalui.uibuilder.Line.line;
import static com.mathieubolla.externalui.uibuilder.UIBuilder.with;

public class ISS3 {
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
	private double skewBack;
	private double skewFront;
	private double offsetStart;

	private Map<Double, Double> offsetsStart;
	private Map<Double, Double> elevations;
	private Map<Double, Double> skewFronts;
	private Map<Double, Double> skewBacks;

	public ISS3() {
		this(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
	}

	public ISS3(double elevation, double skewFront, double skewBack, double offsetStart) {
		this.elevation = elevation;
		this.skewBack = skewBack;
		this.skewFront = skewFront;
		this.offsetStart = offsetStart;

		// -75 21.0 0.0 6.0 355.0
		// -74 21.0 0.0 6.0 355.0
		// -73 15.0 0.0 3.0 355.0 // partial
		// -72 19.0 0.0 0.0 355.0
		// -71 19.0 0.0 0.0 355.0
		// -70 19.0 0.0 0.0   5.0
		// 70 21.0 0.0 6.0   0.0
		// 71 19.0 0.0 0.0 355.0
		// 72 19.0 0.0 0.0   0.0
		// 73 23.0 0.0 6.0   0.0 // partial
		// 74 19.0 0.0 6.0   0.0 // partial
		// 75 21.0 0.0 6.0 355.0

		offsetsStart = new HashMap<Double, Double>();
		offsetsStart.put(-75.0, 355.0);
		offsetsStart.put(-74.0, 355.0);
		offsetsStart.put(-73.0, 355.0);
		offsetsStart.put(-72.0, 355.0);
		offsetsStart.put(-71.0, 355.0);
		offsetsStart.put(-70.0, 5.0);
		offsetsStart.put(70.0, 0.0);
		offsetsStart.put(71.0, 355.0);
		offsetsStart.put(72.0, 0.0);
		offsetsStart.put(73.0, 0.0);
		offsetsStart.put(74.0, 0.0);
		offsetsStart.put(75.0, 355.0);

		elevations = new HashMap<Double, Double>();
		elevations.put(-75.0, 20.8);
		elevations.put(-74.0, 28.3);
		elevations.put(-73.0, 15.9);
		elevations.put(-72.0, 18.2);
		elevations.put(-71.0, 19.1);
		elevations.put(-70.0, 20.0);
		elevations.put(70.0, 21.0);
		elevations.put(71.0, 19.0);
		elevations.put(72.0, 19.0);
		elevations.put(73.0, 23.0);
		elevations.put(74.0, 19.0);
		elevations.put(75.0, 21.0);

		skewFronts = new HashMap<Double, Double>();
		skewFronts.put(-75.0, -30.0);
		skewFronts.put(-74.0, -30.0);
		skewFronts.put(-73.0,  -9.2);
		skewFronts.put(-72.0,  -0.2);
		skewFronts.put(-71.0,  -0.1);
		skewFronts.put(-70.0,  -0.2);
		skewFronts.put(70.0, 0.0);
		skewFronts.put(71.0, 0.0);
		skewFronts.put(72.0, 0.0);
		skewFronts.put(73.0, -18.0);
		skewFronts.put(74.0, -24.0);
		skewFronts.put(75.0, -30.0);

		skewBacks = new HashMap<Double, Double>();
		skewBacks.put(-75.0, -28.9);
		skewBacks.put(-74.0, -28.0);
		skewBacks.put(-73.0, -15.0);
		skewBacks.put(-72.0,   0.8);
		skewBacks.put(-71.0,   0.3);
		skewBacks.put(-70.0,   0.5);
		skewBacks.put(70.0, 0.0);
		skewBacks.put(71.0, 0.0);
		skewBacks.put(72.0, 0.0);
		skewBacks.put(73.0, -18.0);
		skewBacks.put(74.0, -24.0);
		skewBacks.put(75.0, -30.0);
	}

	private static Simulation makeNegativeSimulation(double frontSkew, double backSkew, double offsetStart, double elevation) {
		return new Simulation(
			new Planning(offsetStart, ANGULAR_SPEED).add(constant(Direction.FRONT)),
			new Planning(-offsetStart, ANGULAR_SPEED).add(constant(Direction.BACK)),
			new Planning(limitAngularPosition(REVERSE - elevation), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(elevation + backSkew), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE + elevation), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(-elevation - backSkew), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE + elevation + frontSkew), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(-elevation), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE - elevation - frontSkew), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(elevation), 0.0).fillWith(Sequence.STOPPED)
		);
	}

	private static Simulation makePositiveSimulation(double frontSkew, double backSkew, double offsetStart, double elevation) {
		return new Simulation(
			new Planning(offsetStart, ANGULAR_SPEED).add(constant(Direction.FRONT)),
			new Planning(-offsetStart, ANGULAR_SPEED).add(constant(Direction.BACK)),
			new Planning(limitAngularPosition(elevation + backSkew), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE - elevation), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(-elevation - backSkew), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE + elevation), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(-elevation), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE + elevation + frontSkew), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(elevation), 0.0).fillWith(Sequence.STOPPED),
			new Planning(limitAngularPosition(REVERSE - elevation - frontSkew), 0.0).fillWith(Sequence.STOPPED)
		);
	}

	private Simulation simulation;

	private void setFlightConstants(double beta) {
		elevation = getConstant(elevation, elevations, beta);
		skewFront = getConstant(skewFront, skewFronts, beta);
		skewBack = getConstant(skewBack, skewBacks, beta);
		offsetStart = getConstant(offsetStart, offsetsStart, beta);
	}

	private double getConstant(double currentValue, Map<Double, Double> source, double beta) {
		if (!Double.isNaN(currentValue)) {
			return currentValue;
		}
		return source.get(beta);
	}

	public static void main(String... args) throws Exception {
		final ISS3 iss = new ISS3();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		final String beta = in.readLine();
		System.out.println(iss.getInitialOrientation(Double.parseDouble(beta)));
		System.out.flush();

		final Object lock = new Object();
		ExternalUiProvider provider = new ExternalUiProvider(8080, with(
			line(1,
			     input(1, "elevation", "plus1", "+"),
			     input(1, "elevation", "plus01", ".+"),
			     input(1, "elevation", "minus01", ".-"),
			     input(1, "elevation", "minus1", "-")
			),
			line(1,
			     input(1, "skewFront", "plus1", "+"),
			     input(1, "skewFront", "plus01", ".+"),
			     input(1, "skewFront", "minus01", ".-"),
			     input(1, "skewFront", "minus1", "-")
			),
			line(1,
			     input(1, "skewBack", "plus1", "+"),
			     input(1, "skewBack", "plus01", ".+"),
			     input(1, "skewBack", "minus01", ".-"),
			     input(1, "skewBack", "minus1", "-")
			),
			line(1,
			     input(1, "offset", "plus1", "+"),
			     input(1, "offset", "plus01", ".+"),
			     input(1, "offset", "minus01", ".-"),
			     input(1, "offset", "minus1", "-")
			)
		));
		provider.register(new EventListener() {
			@Override
			public void notify(Event event) {
				synchronized (lock) {
					lock.notify();
				}

				switch (event.getCode()) {
					case "elevation":
						switch (event.getValue()) {
							case "plus1": iss.elevation += 1.0; break;
							case "plus01": iss.elevation += .1; break;
							case "minus1": iss.elevation -= 1.0; break;
							case "minus01": iss.elevation -= .1; break;
						}
						System.err.println("New elevation is "+iss.elevation);
						break;
					case "skewFront":
						switch (event.getValue()) {
							case "plus1": iss.skewFront += 1.0; break;
							case "plus01": iss.skewFront += .1; break;
							case "minus1": iss.skewFront -= 1.0; break;
							case "minus01": iss.skewFront -= .1; break;
						}
						System.err.println("New skewFront is "+iss.skewFront);
						break;
					case "skewBack":
						switch (event.getValue()) {
							case "plus1": iss.skewBack += 1.0; break;
							case "plus01": iss.skewBack += .1; break;
							case "minus1": iss.skewBack -= 1.0; break;
							case "minus01": iss.skewBack -= .1; break;
						}
						System.err.println("New skewBack is "+iss.skewBack);
						break;
					case "offset":
						switch (event.getValue()) {
							case "plus1": iss.offsetStart+= 1.0; break;
							case "plus01": iss.offsetStart += .1; break;
							case "minus1": iss.offsetStart-= 1.0; break;
							case "minus01": iss.offsetStart -= .1; break;
						}
						System.err.println("New offsetStart is "+iss.offsetStart);
						break;
				}
				iss.getInitialOrientation(Double.parseDouble(beta));
			}
		});

		while (true) {
			int minute = 26;

			System.out.println(2);//API
			System.out.println(1);//Render
			System.out.println((360.0 / 92) * minute);//Alpha
			System.out.println(beta);//Beta
			System.out.println(0.0);//Yaw

			double[] states = iss.getStateAtMinute(minute);
			for (int i = 0; i < 20; i += 2) {
				System.out.println(roundedDouble(states[i]));
			}
			System.out.flush();
			for (int i = 0; i < 696; i++) {
				in.readLine();
			}

			synchronized (lock) {
				lock.wait(1000);
			}
		}
	}

	public double getInitialOrientation(double beta) {
		setFlightConstants(beta);

		double yaw = 0.0;

		if (beta < 0) {
			simulation = makeNegativeSimulation(skewFront, skewBack, offsetStart, elevation);
		} else {
			simulation = makePositiveSimulation(skewFront, skewBack, offsetStart, elevation);
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