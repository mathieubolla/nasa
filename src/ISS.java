import java.io.*;

public class ISS {
    public static final double SARJAcceleration = .005; // degree/s^2
    public static final double BGAAcceleration = .01; // degree/s^2

    public static final double SARJVelocity = .15; // degree/s
    public static final double BGAVelocity = .25; // degree/s

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
		return 3.0;
	}

	public double[] getStateAtMinute(int minute) {
        switch(minute) {
            case 0:
                return new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
            case 1:
                return new double[] { 4.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // front pulse sarj
            case 2:
                return new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // reverse pulse sarj
            case 3:
                return new double[] { 6.75, 0.15, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // cold start sarj
            case 4:
                return new double[] { 6.75 + 9.0, 0.15, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // hold sarj
            case 5:
                return new double[] { 6.75 + 9.0, -0.15, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // reverse sarj
            case 6:
                return new double[] { 6.75, -0.15, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // hold back sarj
            case 7:
                return new double[] { 6.75 - 2.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // hot to stop sarj remaining position 4.5
            case 8:
                return new double[] { 6.75 - 2.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // back pulse sarj to go to zero because remaining position = pulse sarj
            case 9:
                return new double[] { 0.0, 0.0, 0.0, 0.0, 8.75, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // front pulse bga
            case 10:
                return new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // back pulse bga
            case 11:
                return new double[] { 0.0, 0.0, 0.0, 0.0, 11.875, 0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // cold start bga
            case 12:
                return new double[] { 0.0, 0.0, 0.0, 0.0, 11.875 + 15, 0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // hold bga
            case 13:
                return new double[] { 0.0, 0.0, 0.0, 0.0, 11.875 + 15 - 2.5, -0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // reverse bga
            case 14:
                return new double[] { 0.0, 0.0, 0.0, 0.0, 11.875 - 2.5, -0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // hold back bga
            case 15:
                return new double[] { 0.0, 0.0, 0.0, 0.0, 11.875 - 2.5 - 3.125, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // hot to stop bga remaining position: 6.25
            case 16:
                return new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }; // back pulse bga to go to zero because remaining position < pulse bga
            default:
                return new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
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

	public static double angle(double a, double b) {
		double delta = limitAngularPosition(b - a);

		if (delta > 180) {
			return delta - 360;
		}

		return delta;
	}
}