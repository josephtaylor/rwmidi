package rwmidi;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a MIDI Controller Change message. The values are parsed into the CC number and the value, which
 * you can access using the methods {@Link Controller-getCC} and {@Link Controller-getValue}.
 */
public class Controller extends MidiEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * Create a Controller Change message.
	 *
	 * @param channel Controller Change channel
	 * @param number  Controller Change number
	 * @param value   Controller Change value
	 */
	public Controller(final int channel, final int number, final int value) {
		super(CONTROL_CHANGE | channel, number, value);
	}

	public Controller(final int number, final int value) {
		super(CONTROL_CHANGE, number, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj || !(obj instanceof Controller)) {
			return false;
		}
		Controller other = (Controller) obj;
		return new EqualsBuilder()
				.append(getCC(), other.getCC())
				.append(getValue(), other.getValue())
				.append(getChannel(), other.getChannel())
				.build();
	}

	/**
	 * @return the CC number of the message
	 */
	public int getCC() {
		return getData1();
	}

	/**
	 * @return the value of the CC message
	 */
	public int getValue() {
		return getData2();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getCC())
				.append(getValue())
				.append(getChannel())
				.build();
	}

	@Override
	public String toString() {
		return "rwmidi.Controller cc: " + getCC() + " value: " + getValue();
	}
}
