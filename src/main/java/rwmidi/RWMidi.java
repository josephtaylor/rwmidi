package rwmidi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

/**
 * RWMidi is a class containing static methods to get a list of the available MIDI devices.
 * You can ask RWMidi about available input and output devices. You can then use the methods
 * on the returned objects to open an input or output midi port.
 * <p/>
 * RWMidi doesn't provide access to the available MIDI devices through an index number, but
 * rather requires you to use the returned MidiDevice object. This is because the number of
 * available devices can change between a call to the function listing the device and the call
 * opening the device, which could potentially lead to an index mismatch.
 */
public class RWMidi {
	/**
	 * Returns a specific input device
	 *
	 * @param name the name of the input device
	 * @return
	 */
	public static MidiInputDevice getInputDevice(String name) {
		List<MidiInputDevice> devices = getInputDevices();
		for (MidiInputDevice device : devices) {
			if (name.equals(device.getName()))
				return device;
		}

		return null;
	}

	/**
	 * @return a list of the output devices names
	 */
	public static String[] getInputDeviceNames() {
		List<MidiInputDevice> devices = getInputDevices();
		ArrayList<String> result = new ArrayList<String>();
		for (MidiInputDevice device : devices) {
			result.add(device.getName());
		}
		return result.toArray(new String[0]);
	}

	/**
	 * @return the list of the available input devices.
	 */
	public static List<MidiInputDevice> getInputDevices() {
		javax.sound.midi.MidiDevice.Info infos[] = MidiSystem.getMidiDeviceInfo();
		List<MidiInputDevice> result = new ArrayList<MidiInputDevice>();
		for (javax.sound.midi.MidiDevice.Info info : infos) {
			javax.sound.midi.MidiDevice device;
			try {
				device = MidiSystem.getMidiDevice(info);
				if (device.getMaxTransmitters() == 0)
					continue;
				result.add(new MidiInputDevice(info));
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
				continue;
			}
		}
		return result;
	}

	/**
	 * Returns a specific output device
	 *
	 * @param name the name of the output device
	 * @return
	 */
	public static MidiOutputDevice getOutputDevice(String name) {
		List<MidiOutputDevice> devices = getOutputDevices();
		for (MidiOutputDevice device : devices) {
			if (name.equals(device.getName()))
				return device;
		}
		return null;
	}

	/**
	 * @return a list of the output devices names
	 */
	public static List<String> getOutputDeviceNames() {
		List<MidiOutputDevice> devices = getOutputDevices();
		List<String> result = new ArrayList<String>();
		for (MidiOutputDevice device : devices) {
			result.add(device.getName());
		}
		return result;
	}

	/**
	 * @return the list of the available output devices.
	 */
	public static List<MidiOutputDevice> getOutputDevices() {
		javax.sound.midi.MidiDevice.Info infos[] = MidiSystem.getMidiDeviceInfo();
		List<MidiOutputDevice> result = new ArrayList<MidiOutputDevice>();
		for (javax.sound.midi.MidiDevice.Info info : infos) {
			javax.sound.midi.MidiDevice device;
			try {
				device = MidiSystem.getMidiDevice(info);
				if (device.getMaxReceivers() == 0)
					continue;
				result.add(new MidiOutputDevice(info));
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
				continue;
			}
		}
		return result;
	}
}
