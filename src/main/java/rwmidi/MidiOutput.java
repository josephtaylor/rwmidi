package rwmidi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

/**
 * Represents a MIDI output used to send MIDI data. This can be a physical MIDI output or a virtual
 * MIDI receiver like the Java Sound Synthesizer.
 */
public class MidiOutput {
	private Receiver receiver;
	private javax.sound.midi.MidiDevice device;

	public MidiOutput(javax.sound.midi.MidiDevice device) throws MidiUnavailableException {
		this.device = device;
		device.open();
		receiver = device.getReceiver();
	}

	public MidiOutput(MidiOutputDevice device) throws MidiUnavailableException {
		this(device.getDevice());
	}

	/**
	 * Close the device associated with this output. This will close other outputs connected to this device as well.
	 */
	public void closeMidi() {
		device.close();
	}

	public String getName() {
		javax.sound.midi.MidiDevice.Info info = device.getDeviceInfo();
		return info.getName() + " " + info.getVendor();
	}

	/**
	 * Send a Controller change message on this output.
	 *
	 * @param channel Channel on which to send the message
	 * @param cc      Controller Change number
	 * @param value   Controller Change value
	 * @return true on success, false on error
	 */
	public boolean sendController(int channel, int cc, int value) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(MidiEvent.CONTROL_CHANGE, channel, cc, value);
			receiver.send(msg, -1);
			return true;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Send a NOTE OFF message on this output.
	 *
	 * @param channel  Channel on which to send the message
	 * @param note     Note pitch
	 * @param velocity Note velocity
	 * @return true on success, false on error
	 */
	public boolean sendNoteOff(int channel, int note, int velocity) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(MidiEvent.NOTE_OFF, channel, note, velocity);
			receiver.send(msg, -1);
			return true;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Send a NOTE ON message on this output.
	 *
	 * @param channel  Channel on which to send the message
	 * @param note     Note pitch
	 * @param velocity Note velocity
	 * @return true on success, false on error
	 */
	public boolean sendNoteOn(int channel, int note, int velocity) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(MidiEvent.NOTE_ON, channel, note, velocity);
			receiver.send(msg, -1);
			return true;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Send a Program Change on this output
	 *
	 * @param value Program Change value
	 * @return true on success, false on error
	 */
	public boolean sendProgramChange(int value) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(MidiEvent.PROGRAM_CHANGE, value, -1);
			receiver.send(msg, -1);
			return true;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendSysex(SysexMessage msg) {
		return sendSysex(msg.getMessage());
	}

	/**
	 * Send a SYSEX MIDI message on this output
	 *
	 * @param msg Bytes of the sysex message, have to contain 0xF0 at the beginning and 0xF7 at the end
	 * @return true on success, false on error
	 */
	public boolean sendSysex(byte[] msg) {
		javax.sound.midi.SysexMessage msg2 = new javax.sound.midi.SysexMessage();
		try {
			msg2.setMessage(msg, msg.length);
			receiver.send(msg2, 0);
			return true;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return false;
		}
	}
}
