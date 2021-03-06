package me.noverish.snmp.snmp.async_task;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Random;

import me.noverish.snmp.MainActivity;
import me.noverish.snmp.net.NetworkClient;
import me.noverish.snmp.packet.PDUType;
import me.noverish.snmp.packet.SNMP;
import me.noverish.snmp.snmp.utils.SNMPPacketBuilder;

public class SNMPSetAsyncTask extends AsyncTask<Void, Void, SNMP> {

    private SNMP packet;
    private SNMPPacketCallback callback;

    public SNMPSetAsyncTask(String oid, String valueType, String value) {
        int requestId = new Random().nextInt(0x7FFFFFFF);

        packet = SNMPPacketBuilder.create(
                MainActivity.COMMUNITY_WRITE,
                PDUType.SET_REQUEST,
                requestId,
                oid,
                valueType,
                value
        );
    }

    @Override
    protected void onPreExecute() {
        if (callback != null && packet != null)
            callback.onSNMPPacketSent(packet);
    }

    @Override
    protected SNMP doInBackground(Void... voids) {
        try {
            return NetworkClient.sendSNMP(MainActivity.HOST, MainActivity.PORT, packet);
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(SNMP packet) {
        if (callback != null && packet != null)
            callback.onSNMPPacketReceived(packet);
    }

    public SNMPSetAsyncTask setCallback(SNMPPacketCallback listener) {
        this.callback = listener;
        return this;
    }
}
