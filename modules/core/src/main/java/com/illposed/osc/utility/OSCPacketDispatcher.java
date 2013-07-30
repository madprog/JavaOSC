/*
 * Copyright (C) 2003, C. Ramakrishnan / Auracle.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.illposed.osc.utility;

import java.net.SocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;

/**
 * Dispatches OSCMessages to registered listeners.
 *
 * @author Chandrasekhar Ramakrishnan
 */

public class OSCPacketDispatcher {

	private Map<String, OSCListener> addressToListener
			= new HashMap<String, OSCListener>();

	/**
	 *
	 */
	public OSCPacketDispatcher() {
	}

	public void addListener(String address, OSCListener listener) {
		addressToListener.put(address, listener);
	}

	public void dispatchPacket(OSCPacket packet, SocketAddress origin) {
		if (packet instanceof OSCBundle) {
			dispatchBundle((OSCBundle) packet, origin);
		} else {
			dispatchMessage((OSCMessage) packet, origin);
		}
	}

	public void dispatchPacket(OSCPacket packet, Date timestamp, SocketAddress origin) {
		if (packet instanceof OSCBundle) {
			dispatchBundle((OSCBundle) packet, origin);
		} else {
			dispatchMessage((OSCMessage) packet, timestamp, origin);
		}
	}

	private void dispatchBundle(OSCBundle bundle, SocketAddress origin) {
		Date timestamp = bundle.getTimestamp();
		OSCPacket[] packets = bundle.getPackets();
		for (OSCPacket packet : packets) {
			dispatchPacket(packet, timestamp, origin);
		}
	}

	private void dispatchMessage(OSCMessage message, SocketAddress origin) {
		dispatchMessage(message, null, origin);
	}

	private void dispatchMessage(OSCMessage message, Date time, SocketAddress origin) {
		for (Entry<String, OSCListener> addrList : addressToListener.entrySet()) {
			if (message.getAddress().matches(addrList.getKey())) {
				addrList.getValue().acceptMessage(time, message, origin);
			}
		}
	}
}
