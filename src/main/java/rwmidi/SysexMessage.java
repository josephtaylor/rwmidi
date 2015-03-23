package rwmidi;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This is a wrapper around a SysexMessage. You can access the bytes using getMessage().
 */
public class SysexMessage extends MidiEvent {
	private static final long serialVersionUID = 1L;

	protected SysexMessage(javax.sound.midi.SysexMessage sysexMessage) {
		super(sysexMessage);
	}

	/**
	 * Create a sysex message from the given bytes
	 *
	 * @param data sysex bytes (must contain 0xF0 and 0xF7)
	 */
	public SysexMessage(byte[] data) {
		super(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj || !(obj instanceof SysexMessage)) {
			return false;
		}
		return new EqualsBuilder().append(getMessage(), ((SysexMessage) obj).getMessage()).build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getMessage()).build();
	}

	@Override
	public String toString() {
		return "Sysex Message: \n" + DatatypeConverter.printHexBinary(getMessage());
	}
}
