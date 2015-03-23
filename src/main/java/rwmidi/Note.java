/*
Part of the proMIDI lib - http://texone.org/promidi

Copyright (c) 2005 Christian Riekoff

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General
Public License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330,
Boston, MA  02111-1307  USA
*/

package rwmidi;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Wrapper around Note MIDI messages. You can extract the pitch using the {@Link Note-getPitch} and the velocity using
 * {@Link Note-getVelocity}.
 */
public class Note extends MidiEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * Create a Note object.
	 *
	 * @param pitch    the pitch of the note
	 * @param velocity the velocity of the note
	 */
	public Note(final int pitch, final int velocity) {
		super(NOTE_ON, pitch, velocity);
	}

	public Note(int midiChannel, int pitch, int velocity) {
		super(NOTE_ON | midiChannel, pitch, velocity);
	}

	public Note(int midiCommand, int midiChannel, int pitch, int velocity) {
		super(midiCommand | midiChannel, pitch, velocity);
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj || !(obj instanceof Note)) {
			return false;
		}
		return new EqualsBuilder()
				.append(getChannel(), ((Note) obj).getChannel())
				.append(getPitch(), ((Note) obj).getPitch())
				.append(getVelocity(), ((Note) obj).getVelocity())
				.build();
	}

	/**
	 * @return Pitch of the note
	 */
	public int getPitch() {
		return getData1();
	}

	/**
	 * @return Velocity of the note
	 */
	public int getVelocity() {
		return getData2();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getChannel())
				.append(getPitch())
				.append(getVelocity())
				.build();
	}

	void setPitch(final int pitch) {
		setData1(pitch);
	}

	void setVelocity(final int velocity) {
		setData2(velocity);
	}

	@Override
	public String toString() {
		return "rwmidi.Note pitch: " + getPitch() + " velocity: " + getVelocity();
	}
}
