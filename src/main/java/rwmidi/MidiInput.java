package rwmidi;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.xml.bind.DatatypeConverter;

/**
 * Represents a MIDI input used to receive MIDI data. This can be either a physical MIDI Input or a virtual device
 * like the Java Sequencer. Open a MidiInput by using {@link MidiInputDevice-createInput}, and use the {@link MidiInput-plug-Object} method
 * to register callbacks to your objects. Use {@link MidiInput-close} to clear the callback list, and use {@link MidiInput-closeMidi}
 * to close the corresponding MidiDevice (however, this will close all MidiInputs connected to this device).
 */
public class MidiInput implements Receiver {

	javax.sound.midi.MidiDevice jDevice;
	ArrayList<Plug> plugList;
	ArrayList currentMessage;

	/**
	 * Create a MidiInput from a javax.sound.midi.MidiDevice .
	 *
	 * @param device
	 * @throws MidiUnavailableException
	 */
	public MidiInput(javax.sound.midi.MidiDevice device) throws MidiUnavailableException {
		this.jDevice = device;
		device.open();
		Transmitter trsmt = device.getTransmitter();
		trsmt.setReceiver(this);
		plugList = new ArrayList<Plug>();
		currentMessage = new ArrayList();
		System.out.println("Foo");
	}

	protected MidiInput(MidiInputDevice device) throws MidiUnavailableException {
		this(device.getDevice());
	}

	/**
	 * Close the MIDI input.
	 */
	public void close() {
		plugList.clear();
	}

	/**
	 * Close the MIDI device attached to this input. This will close all the other inputs as well.
	 */
	public void closeMidi() {
		jDevice.close();
	}

	public String getName() {
		javax.sound.midi.MidiDevice.Info info = jDevice.getDeviceInfo();
		return info.getName() + " " + info.getVendor();
	}

	/**
	 * Register a callback method on a specific channel for a specific MIDI command. The value field is the MIDI status byte,
	 * for example 0x90 for NOTE ON.
	 *
	 * @param object     Callback object
	 * @param methodName Name of the method of the callback object that will be called by the input
	 * @param channel    Channel of the message, -1 for all channels
	 * @param value      MIDI status byte, -1 for all messages
	 */
	public void plug(final Object object, final String methodName, final int channel, final int value) {
		if (Plug.objectHasMethod(object, methodName)) {
			Plug plug = new Plug(object, methodName, channel, value);
			plugList.add(plug);
		}
	}

	/**
	 * Register a callback method for all MIDI messages received on this input.
	 *
	 * @param object     Callback object
	 * @param methodName Name of the method of the callback object that will be called by the input
	 */
	public void plug(final Object object, final String methodName) {
		plug(object, methodName, -1, -1);
	}

	/**
	 * Register a callback method for all MIDI messages received on a specific channel on this input.
	 *
	 * @param object     Callback object
	 * @param methodName Name of the method of the callback object that will be called by the input.
	 * @param channel    Channel on which the messages are going to be received
	 */
	public void plug(final Object object, final String methodName, final int channel) {
		plug(object, methodName, channel, -1);
	}

	/**
	 * Register an object with standard midi callbacks on all channels. The callbacks are noteOnReceived(Note),
	 * noteOffReceived(Note), controllerChangeReceived(Controller), programChangeReceived(ProgramChange) and
	 * sysexReceived(SysexMessage).
	 *
	 * @param obj the object with standard callbacks
	 */
	public void plug(Object obj) {
		plug(obj, -1);
	}

	/**
	 * Register an object with standard midi callbacks on a specific channels. The callbacks are noteOnReceived(Note),
	 * noteOffReceived(Note), controllerChangeReceived(Controller), programChangeReceived(ProgramChange) and
	 * sysexReceived(SysexMessage).
	 *
	 * @param obj     the object with standard callbacks
	 * @param channel the channel on which to receive note and controller change messages
	 */
	public void plug(Object obj, int channel) {
		if (obj == null) {
			return;
		}
		plug(obj, "noteOnReceived", channel, MidiEvent.NOTE_ON);
		plug(obj, "noteOffReceived", channel, MidiEvent.NOTE_OFF);
		plug(obj, "controllerChangeReceived", channel, MidiEvent.CONTROL_CHANGE);
		plug(obj, "programChangeReceived", -1, MidiEvent.PROGRAM_CHANGE);
		plug(obj, "sysexReceived", -1, MidiEvent.SYSEX_START);
	}

	public void send(final MidiMessage message, final long timeStamp) {
		if ((message.getLength() > 1)) {
			System.out.println("message " + message + " " + DatatypeConverter.printHexBinary(message.getMessage()));
		}
		if (message instanceof javax.sound.midi.SysexMessage || message.getStatus() == (byte) 0xF7) {
			if (message.getStatus() == 0xF0) {
				currentMessage.clear(); // no shortcut for sysex messages
				currentMessage.add((byte) 0xF0);
			}
			for (byte b : ((javax.sound.midi.SysexMessage) message).getData()) {
				if (b == (byte) 0xF7) {
					currentMessage.add((byte) 0xF7);
					byte messageBytes[] = new byte[currentMessage.size()];
					for (int i = 0; i < currentMessage.size(); i++) {
						messageBytes[i] = ((Byte) currentMessage.get(i)).byteValue();
					}
					javax.sound.midi.SysexMessage newMessage = new javax.sound.midi.SysexMessage();
					try {
						newMessage.setMessage(messageBytes, messageBytes.length);
						for (Plug plug : plugList) {
							plug.callPlug(this, newMessage);
						}
					} catch (InvalidMidiDataException e) {
						throw new RuntimeException("Failed to send sysex message.", e);
					}
				} else {
					currentMessage.add(b);
				}
			}
		} else {
			if (message.getStatus() >= 0xF8) {
				return;
			} else {
				currentMessage.clear(); // discard maybe sysex message
			}
			for (Plug plug : plugList)
				plug.callPlug(this, message);
		}
	}

	public void unplug(Object obj) {
		for (Plug plug : plugList) {
			if (plug.getObject().equals(obj))
				plugList.remove(plug);
		}
	}

	public void unplug(Object obj, int channel) {
		for (Plug plug : plugList) {
			if (plug.getObject().equals(obj) && plug.getChannel() == channel)
				plugList.remove(plug);
		}
	}

	public void unplug(Object obj, String methodName, int channel) {
		for (Plug plug : plugList) {
			if (plug.getObject().equals(obj) && plug.getChannel() == channel && plug.getMethodName().equals(methodName))
				plugList.remove(plug);
		}
	}


}
