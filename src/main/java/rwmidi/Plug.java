package rwmidi;

import java.lang.reflect.Method;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 * Wrapper class for callback plugs. You don't usually need to access this class.
 *
 * @author manuel
 */

class Plug {
	private final Method method;
	private final String methodName;
	private final Object object;
	private final Class objectClass;
	private Class paramClass;

	private int status = -1;
	private int channel = -1;

	Plug(final Object object, final String methodName, final int channel, final int status) {
		this.object = object;
		objectClass = getObject().getClass();
		this.methodName = methodName;
		method = initPlug();
		setStatus(status);
		this.channel = channel;
	}

	static boolean objectHasMethod(Object obj, String methodName) {
		Class objectClass = obj.getClass();
		for (Method method : objectClass.getDeclaredMethods()) {
			if (method.getName().equals(methodName))
				return true;
		}
		return false;
	}

	void callPlug(MidiInput _input, final MidiMessage midiMessage) {
		try {
			if ((midiMessage.getStatus() & 0xF0) != getStatus() && getStatus() != -1)
				return;
			if (midiMessage instanceof ShortMessage) {
				ShortMessage smsg = (ShortMessage) midiMessage;
				if (smsg.getChannel() != channel && channel != -1)
					return;
			}
			MidiEvent event;
			if (midiMessage instanceof MidiEvent) {
				event = (MidiEvent) midiMessage;
			} else {
				event = MidiEvent.create(midiMessage);
			}

			if (event != null) {
				event.setInput(_input);
				if (paramClass.isInstance(event)) {
					method.invoke(getObject(), new Object[] { event });
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error on calling plug: " + methodName, e);
		}
	}

	protected int getChannel() {
		return channel;
	}

	protected String getMethodName() {
		return methodName;
	}

	protected Object getObject() {
		return object;
	}

	protected int getStatus() {
		return status;
	}

	private Method initPlug() {
		if (methodName != null && methodName.length() > 0) {
			final Method[] objectMethods = objectClass.getDeclaredMethods();

			for (int i = 0; i < objectMethods.length; i++) {
				objectMethods[i].setAccessible(true);

				if (objectMethods[i].getName().equals(methodName)) {
					final Class[] objectMethodParams = objectMethods[i].getParameterTypes();
					if (objectMethodParams.length == 1) {
						paramClass = objectMethodParams[0];
						try {
							return objectClass.getDeclaredMethod(methodName, objectMethodParams);
						} catch (Exception e) {
							throw new RuntimeException("Error on plug: >" + methodName + "< Invalid argument class", e);
						}
					} else {
						break;
					}
				}
			}
		}
		return null;
	}

	protected void setChannel(int channel) {
		this.channel = channel;
	}

	protected void setStatus(int status) {
		this.status = status;
	}
}
