package me.noverish.snmp.packet;

import android.support.annotation.Nullable;

import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.asn1.BERSerializable;

import java.io.IOException;
import java.io.OutputStream;

import me.noverish.snmp.snmp.utils.BERLengthUtil;

public class PDUVariableValue implements BERSerializable {
    public Integer intValue = null;
    public String stringValue = null;
    public PDUVariableOID oidValue = null;
    public Long timeTickValue = null;
    public Long gauge32Value = null;
    public Long counter32Value = null;

    public Boolean noSuchObject = null;
    public Boolean noSuchInstance = null;
    public Boolean isEnd = null;

    public PDUVariableValue() {

    }

    public PDUVariableValue(@Nullable String valueType, @Nullable String value) {
        if (valueType != null && value != null)
            switch (valueType) {
                case "Integer":
                    intValue = Integer.parseInt(value);
                    break;
                case "String":
                    stringValue = value;
                    break;
                case "ObjectID":
                    oidValue = new PDUVariableOID(value);
                    break;
                case "TimeTick":
                    timeTickValue = Long.parseLong(value);
                    break;
                case "Gauge32":
                    gauge32Value = Long.parseLong(value);
                    break;
                case "Counter32":
                    counter32Value = Long.parseLong(value);
                    break;
            }
    }

    // BERSerializable
    @Override
    public void encodeBER(OutputStream os) throws IOException {
        if (intValue != null)
            BER.encodeInteger(os, BER.INTEGER, intValue);
        else if (stringValue != null)
            BER.encodeString(os, BER.OCTETSTRING, stringValue.getBytes());
        else if (oidValue != null)
            oidValue.encodeBER(os);
        else if (timeTickValue != null)
            BER.encodeUnsignedInteger(os, BER.TIMETICKS, timeTickValue);
        else if (gauge32Value != null)
            BER.encodeUnsignedInteger(os, BER.TIMETICKS, gauge32Value);
        else if (counter32Value != null)
            BER.encodeUnsignedInteger(os, BER.TIMETICKS, counter32Value);
        else if (noSuchObject != null)
            BER.encodeHeader(os, BER.NOSUCHOBJECT, getBERPayloadLength());
        else if (noSuchInstance != null)
            BER.encodeHeader(os, BER.NOSUCHINSTANCE, getBERPayloadLength());
        else if (isEnd != null)
            BER.encodeHeader(os, BER.ENDOFMIBVIEW, getBERPayloadLength());
        else
            BER.encodeHeader(os, BER.ASN_NULL, getBERPayloadLength());
    }

    @Override
    public void decodeBER(BERInputStream is) throws IOException {
        byte type = is.getBuffer().array()[(int) is.getPosition()];
        if (type == BER.INTEGER) {
            intValue = BER.decodeInteger(is, new BER.MutableByte());
        } else if (type == BER.OCTETSTRING) {
            stringValue = new String(BER.decodeString(is, new BER.MutableByte()));
        } else if (type == BER.OID) {
            oidValue = new PDUVariableOID();
            oidValue.decodeBER(is);
        } else if (type == BER.TIMETICKS) {
            timeTickValue = BER.decodeUnsignedInteger(is, new BER.MutableByte());
        } else if (type == BER.GAUGE32) {
            gauge32Value = BER.decodeUnsignedInteger(is, new BER.MutableByte());
        } else if (type == BER.COUNTER32) {
            counter32Value = BER.decodeUnsignedInteger(is, new BER.MutableByte());
        } else if (type == BER.NULL) {
            BER.decodeHeader(is, new BER.MutableByte());
        } else if (type == (byte) BER.NOSUCHOBJECT) {
            BER.decodeHeader(is, new BER.MutableByte());
            noSuchObject = true;
        } else if (type == (byte) BER.NOSUCHINSTANCE) {
            BER.decodeHeader(is, new BER.MutableByte());
            noSuchInstance = true;
        } else if (type == (byte) BER.ENDOFMIBVIEW) {
            BER.decodeHeader(is, new BER.MutableByte());
            isEnd = true;
        }
    }

    @Override
    public int getBERLength() {
        int payloadLength = getBERPayloadLength();
        return payloadLength + BER.getBERLengthOfLength(payloadLength) + 1;
    }

    @Override
    public int getBERPayloadLength() {
        if (intValue != null)
            return BERLengthUtil.getLengthOfInteger(intValue);
        else if (stringValue != null)
            return stringValue.length();
        else if (oidValue != null)
            return oidValue.getBERPayloadLength();
        else if (timeTickValue != null)
            return BERLengthUtil.getLengthOfUnsignedInteger(timeTickValue);
        else if (gauge32Value != null)
            return BERLengthUtil.getLengthOfUnsignedInteger(gauge32Value);
        else if (counter32Value != null)
            return BERLengthUtil.getLengthOfUnsignedInteger(counter32Value);
        else if (noSuchObject != null || noSuchInstance != null || isEnd != null)
            return 0;
        else
            return 0;
    }


    // toString
    @Override
    public String toString() {
        if (intValue != null)
            return "{ Integer: " + intValue + " }";
        else if (stringValue != null)
            return "{ String: " + stringValue + " }";
        else if (oidValue != null)
            return "{ OID: " + oidValue.toString() + " }";
        else if (timeTickValue != null)
            return "{ TimeTick: " + timeTickValue + " }";
        else if (gauge32Value != null)
            return "{ Gauge32: " + gauge32Value + " }";
        else if (counter32Value != null)
            return "{ Counter32: " + counter32Value + " }";
        else if (noSuchObject != null)
            return "{ NoSuchObject }";
        else if (noSuchInstance != null)
            return "{ NoSuchInstance }";
        else if (isEnd != null)
            return "{ EndOfMibWindow }";
        else
            return "null";
    }
}
