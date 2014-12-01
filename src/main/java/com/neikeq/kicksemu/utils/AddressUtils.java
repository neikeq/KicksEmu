package com.neikeq.kicksemu.utils;

import sun.net.util.IPAddressUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AddressUtils {

    public static boolean isValidAddressOrHost(String value) {
        return isValidAddress(value) || isValidHost(value);
    }

    public static boolean isValidAddress(String address) {
        return IPAddressUtil.isIPv4LiteralAddress(address) ||
                IPAddressUtil.isIPv6LiteralAddress(address);
    }

    public static boolean isValidHost(String host) {
        try {
            return InetAddress.getByName(host) != null;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
