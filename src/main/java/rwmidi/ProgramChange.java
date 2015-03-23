package rwmidi;

import javax.sound.midi.ShortMessage;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Wrapper around Program Change messages. You can access the Program Change number by using the
 * {@Link ProgramChange-getNumber} method.
 */
public class ProgramChange extends MidiEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * Create a Program Change message
	 *
	 * @param number the program change number
	 */
	public ProgramChange(final int number) {
		super(ShortMessage.PROGRAM_CHANGE, number, -1);
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj || !(obj instanceof ProgramChange)) {
			return false;
		}
		return getNumber() == ((ProgramChange) obj).getNumber();
	}

	/**
	 * @return the program change number of this message
	 */
	public int getNumber() {
		return getData1();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getNumber()).build();
	}

	@Override
	public String toString() {
		return "rwmidi.ProgramChange " + getNumber();
	}
}
